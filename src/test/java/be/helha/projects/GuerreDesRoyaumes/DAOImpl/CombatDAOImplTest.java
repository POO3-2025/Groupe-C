package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.Model.Combat.Combat;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CombatDAOImplTest {

    private TestConnection connection;
    private TestPreparedStatement preparedStatement;
    private TestResultSet resultSet;
    private TestJoueurDAOImpl joueurDAO;
    private TestRoyaumeMongoDAOImpl royaumeMongoDAO;
    
    private CombatDAOImpl combatDAO;
    
    private Joueur joueur1;
    private Joueur joueur2;
    private Combat combat;
    private Royaume royaume;
    
    @BeforeEach
    void setUp() throws Exception {
        // Initialiser les composants de test
        resultSet = new TestResultSet();
        preparedStatement = new TestPreparedStatement(resultSet);
        connection = new TestConnection(preparedStatement);
        joueurDAO = new TestJoueurDAOImpl();
        royaumeMongoDAO = new TestRoyaumeMongoDAOImpl();
        
        // Initialiser les joueurs et le combat
        joueur1 = new Joueur();
        joueur1.setId(1);
        joueur1.setPseudo("Joueur1");
        joueur1.setVictoires(5);
        joueur1.setDefaites(2);
        
        joueur2 = new Joueur();
        joueur2.setId(2);
        joueur2.setPseudo("Joueur2");
        joueur2.setVictoires(3);
        joueur2.setDefaites(4);
        
        royaume = new Royaume(1, "Royaume du Joueur1", 1);
        joueur1.setRoyaume(royaume);
        
        combat = new Combat(1, joueur1, joueur2);
        combat.setVainqueur(joueur1);
        combat.setNombreTours(5);
        
        // Configuration des données de test
        joueurDAO.addJoueur(joueur1);
        joueurDAO.addJoueur(joueur2);
        royaumeMongoDAO.addRoyaume(royaume);
        
        // Initialiser le CombatDAO avec nos dépendances de test
        combatDAO = new CombatDAOImpl();
        combatDAO.setConnection(connection);
        
        // Préparation du ResultSet pour les tests
        resultSet.setNextReturns(true); // Par défaut, next() retourne true
        resultSet.addIntColumn("id_demandeur", 1);
        resultSet.addIntColumn("argent", 100);
    }
    
    @Test
    void testEnregistrerCombat() throws SQLException {
        // Act
        combatDAO.enregistrerCombat(combat);
        
        // Assert
        assertTrue(preparedStatement.executedUpdate);
        assertEquals(5, preparedStatement.parameterIndex);
        assertEquals(combat.getId(), preparedStatement.parameters.get(1));
        assertEquals(joueur1.getId(), preparedStatement.parameters.get(2));
        assertEquals(joueur2.getId(), preparedStatement.parameters.get(3));
        assertEquals(joueur1.getId(), preparedStatement.parameters.get(4));
        assertEquals(combat.getNombreTours(), preparedStatement.parameters.get(5));
    }
    
    @Test
    void testEnregistrerVictoire() throws SQLException {
        // Act
        combatDAO.enregistrerVictoire(joueur1);
        
        // Assert
        assertTrue(preparedStatement.executedUpdate);
        assertEquals(1, preparedStatement.parameterIndex);
        assertEquals(joueur1.getId(), preparedStatement.parameters.get(1));
    }
    
    @Test
    void testEnregistrerDefaite() throws SQLException {
        // Act
        combatDAO.enregistrerDefaite(joueur2);
        
        // Assert
        assertTrue(preparedStatement.executedUpdate);
        assertEquals(1, preparedStatement.parameterIndex);
        assertEquals(joueur2.getId(), preparedStatement.parameters.get(1));
    }
    
    @Test
    void testEnvoyerDemandeCombat() throws SQLException {
        // Arrange
        resultSet.setNextReturns(false); // Pas de demande existante
        
        // Act
        boolean result = combatDAO.envoyerDemandeCombat(1, 2);
        
        // Assert
        assertTrue(result);
        assertTrue(preparedStatement.executedUpdate);
    }
    
    @Test
    void testEnvoyerDemandeCombat_DemandeExistante() throws SQLException {
        // Arrange - Une demande existe déjà (next() retourne true par défaut)
        resultSet.addIntColumn("", 1); // La demande compte = 1
        
        // Act
        boolean result = combatDAO.envoyerDemandeCombat(1, 2);
        
        // Assert
        assertTrue(result);
        // Pas d'exécution de mise à jour quand la demande existe déjà
        assertFalse(preparedStatement.executedUpdate);
    }
    
    @Test
    void testVerifierDemandesCombat() throws SQLException {
        // Act
        int result = combatDAO.verifierDemandesCombat(2);
        
        // Assert
        assertEquals(1, result); // id_demandeur = 1
        assertTrue(preparedStatement.executedQuery);
    }
    
    @Test
    void testVerifierDemandesCombat_PasDeDemande() throws SQLException {
        // Arrange
        resultSet.setNextReturns(false); // Pas de demande
        
        // Act
        int result = combatDAO.verifierDemandesCombat(2);
        
        // Assert
        assertEquals(0, result);
        assertTrue(preparedStatement.executedQuery);
    }
    
    @Test
    void testAccepterDemandeCombat() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true); // La demande existe
        when(resultSet.getInt(1)).thenReturn(1); // Le compte de demandes est 1
        
        // Act
        boolean result = combatDAO.accepterDemandeCombat(1, 2);
        
        // Assert
        assertTrue(result);
    }
    
    @Test
    void testAccepterDemandeCombat_PasDeDemande() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true); // Première vérification passe
        when(resultSet.getInt(1)).thenReturn(0); // Mais aucune demande n'existe
        
        // Act
        boolean result = combatDAO.accepterDemandeCombat(1, 2);
        
        // Assert
        assertFalse(result);
    }
    
    @Test
    void testSupprimerDemandeCombat() throws SQLException {
        // Act
        boolean result = combatDAO.supprimerDemandeCombat(1, 2);
        
        // Assert
        assertTrue(result);
        verify(preparedStatement, times(1)).setInt(1, 1);
        verify(preparedStatement, times(1)).setInt(2, 2);
        verify(preparedStatement, times(1)).executeUpdate();
    }
    
    @Test
    void testAppliquerSanctionFinanciere_AvecSuffisantArgent() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("argent")).thenReturn(100);
        
        // Act
        boolean result = combatDAO.appliquerSanctionFinanciere(1, 50);
        
        // Assert
        assertTrue(result);
        verify(preparedStatement, times(1)).setInt(1, 50); // Nouvel argent (100 - 50)
        verify(preparedStatement, times(1)).setInt(2, 1); // ID joueur
    }
    
    @Test
    void testAppliquerSanctionFinanciere_AvecInsufficientArgent() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("argent")).thenReturn(30);
        
        // Act
        boolean result = combatDAO.appliquerSanctionFinanciere(1, 50);
        
        // Assert
        assertTrue(result);
        verify(preparedStatement, times(1)).setInt(1, 0); // Nouvel argent (min 0)
        verify(preparedStatement, times(1)).setInt(2, 1); // ID joueur
    }
    
    @Test
    void testAppliquerSanctionFinanciere_JoueurNonTrouve() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(false);
        
        // Act
        boolean result = combatDAO.appliquerSanctionFinanciere(1, 50);
        
        // Assert
        assertFalse(result);
    }
    
    // Classes d'implémentation pour les tests
    
    private static class TestConnection implements Connection {
        private final TestPreparedStatement preparedStatement;
        private boolean closed = false;
        
        public TestConnection(TestPreparedStatement preparedStatement) {
            this.preparedStatement = preparedStatement;
        }
        
        @Override
        public PreparedStatement prepareStatement(String sql) {
            preparedStatement.setSql(sql);
            return preparedStatement;
        }
        
        @Override
        public void close() {
            closed = true;
        }
        
        @Override
        public boolean isClosed() {
            return closed;
        }
        
        @Override
        public DatabaseMetaData getMetaData() {
            throw new UnsupportedOperationException("Not implemented for test");
        }
        
        // Méthodes non implémentées pour les tests
        @Override public java.sql.Statement createStatement() { throw new UnsupportedOperationException(); }
        @Override public java.sql.CallableStatement prepareCall(String sql) { throw new UnsupportedOperationException(); }
        @Override public String nativeSQL(String sql) { throw new UnsupportedOperationException(); }
        @Override public void setAutoCommit(boolean autoCommit) { }
        @Override public boolean getAutoCommit() { return false; }
        @Override public void commit() { }
        @Override public void rollback() { }
        @Override public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency) { throw new UnsupportedOperationException(); }
        @Override public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) { throw new UnsupportedOperationException(); }
        @Override public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) { throw new UnsupportedOperationException(); }
        @Override public java.util.Map<String, Class<?>> getTypeMap() { throw new UnsupportedOperationException(); }
        @Override public void setTypeMap(java.util.Map<String, Class<?>> map) { throw new UnsupportedOperationException(); }
        @Override public void setHoldability(int holdability) { throw new UnsupportedOperationException(); }
        @Override public int getHoldability() { throw new UnsupportedOperationException(); }
        @Override public java.sql.Savepoint setSavepoint() { throw new UnsupportedOperationException(); }
        @Override public java.sql.Savepoint setSavepoint(String name) { throw new UnsupportedOperationException(); }
        @Override public void rollback(java.sql.Savepoint savepoint) { throw new UnsupportedOperationException(); }
        @Override public void releaseSavepoint(java.sql.Savepoint savepoint) { throw new UnsupportedOperationException(); }
        @Override public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) { throw new UnsupportedOperationException(); }
        @Override public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) { throw new UnsupportedOperationException(); }
        @Override public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) { throw new UnsupportedOperationException(); }
        @Override public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) { throw new UnsupportedOperationException(); }
        @Override public PreparedStatement prepareStatement(String sql, int[] columnIndexes) { throw new UnsupportedOperationException(); }
        @Override public PreparedStatement prepareStatement(String sql, String[] columnNames) { throw new UnsupportedOperationException(); }
        @Override public java.sql.Clob createClob() { throw new UnsupportedOperationException(); }
        @Override public java.sql.Blob createBlob() { throw new UnsupportedOperationException(); }
        @Override public java.sql.NClob createNClob() { throw new UnsupportedOperationException(); }
        @Override public java.sql.SQLXML createSQLXML() { throw new UnsupportedOperationException(); }
        @Override public boolean isValid(int timeout) { throw new UnsupportedOperationException(); }
        @Override public void setClientInfo(String name, String value) { throw new UnsupportedOperationException(); }
        @Override public void setClientInfo(java.util.Properties properties) { throw new UnsupportedOperationException(); }
        @Override public String getClientInfo(String name) { throw new UnsupportedOperationException(); }
        @Override public java.util.Properties getClientInfo() { throw new UnsupportedOperationException(); }
        @Override public java.sql.Array createArrayOf(String typeName, Object[] elements) { throw new UnsupportedOperationException(); }
        @Override public java.sql.Struct createStruct(String typeName, Object[] attributes) { throw new UnsupportedOperationException(); }
        @Override public void setSchema(String schema) { throw new UnsupportedOperationException(); }
        @Override public String getSchema() { throw new UnsupportedOperationException(); }
        @Override public void abort(java.util.concurrent.Executor executor) { throw new UnsupportedOperationException(); }
        @Override public void setNetworkTimeout(java.util.concurrent.Executor executor, int milliseconds) { throw new UnsupportedOperationException(); }
        @Override public int getNetworkTimeout() { throw new UnsupportedOperationException(); }
        @Override public <T> T unwrap(Class<T> iface) { throw new UnsupportedOperationException(); }
        @Override public boolean isWrapperFor(Class<?> iface) { throw new UnsupportedOperationException(); }
    }
    
    private static class TestPreparedStatement implements PreparedStatement {
        private final TestResultSet resultSet;
        private boolean executedQuery = false;
        private boolean executedUpdate = false;
        private String sql = "";
        private final java.util.Map<Integer, Object> parameters = new java.util.HashMap<>();
        private int parameterIndex = 0;
        
        public TestPreparedStatement(TestResultSet resultSet) {
            this.resultSet = resultSet;
        }
        
        public void setSql(String sql) {
            this.sql = sql;
        }
        
        @Override
        public ResultSet executeQuery() throws SQLException {
            executedQuery = true;
            return resultSet;
        }
        
        @Override
        public int executeUpdate() throws SQLException {
            executedUpdate = true;
            return 1; // Simuler que 1 ligne a été affectée
        }
        
        @Override
        public void setInt(int parameterIndex, int x) throws SQLException {
            parameters.put(parameterIndex, x);
            this.parameterIndex = parameterIndex;
        }
        
        @Override
        public void setObject(int parameterIndex, Object x) throws SQLException {
            parameters.put(parameterIndex, x);
            this.parameterIndex = parameterIndex;
        }
        
        // Méthodes non implémentées pour les tests
        @Override public java.sql.ResultSetMetaData getMetaData() { throw new UnsupportedOperationException(); }
        @Override public void setNull(int parameterIndex, int sqlType) { throw new UnsupportedOperationException(); }
        @Override public void setBoolean(int parameterIndex, boolean x) { 
            parameters.put(parameterIndex, x);
            this.parameterIndex = parameterIndex;
        }
        @Override public void setByte(int parameterIndex, byte x) { throw new UnsupportedOperationException(); }
        @Override public void setShort(int parameterIndex, short x) { throw new UnsupportedOperationException(); }
        @Override public void setLong(int parameterIndex, long x) { throw new UnsupportedOperationException(); }
        @Override public void setFloat(int parameterIndex, float x) { throw new UnsupportedOperationException(); }
        @Override public void setDouble(int parameterIndex, double x) { throw new UnsupportedOperationException(); }
        @Override public void setBigDecimal(int parameterIndex, java.math.BigDecimal x) { throw new UnsupportedOperationException(); }
        @Override public void setString(int parameterIndex, String x) { 
            parameters.put(parameterIndex, x);
            this.parameterIndex = parameterIndex;
        }
        @Override public void setBytes(int parameterIndex, byte[] x) { throw new UnsupportedOperationException(); }
        @Override public void setDate(int parameterIndex, java.sql.Date x) { throw new UnsupportedOperationException(); }
        @Override public void setTime(int parameterIndex, java.sql.Time x) { throw new UnsupportedOperationException(); }
        @Override public void setTimestamp(int parameterIndex, java.sql.Timestamp x) { throw new UnsupportedOperationException(); }
        @Override public void setAsciiStream(int parameterIndex, java.io.InputStream x, int length) { throw new UnsupportedOperationException(); }
        @Override public void setUnicodeStream(int parameterIndex, java.io.InputStream x, int length) { throw new UnsupportedOperationException(); }
        @Override public void setBinaryStream(int parameterIndex, java.io.InputStream x, int length) { throw new UnsupportedOperationException(); }
        @Override public void clearParameters() { throw new UnsupportedOperationException(); }
        @Override public void setObject(int parameterIndex, Object x, int targetSqlType) { throw new UnsupportedOperationException(); }
        @Override public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) { throw new UnsupportedOperationException(); }
        @Override public boolean execute() { throw new UnsupportedOperationException(); }
        @Override public void addBatch() { throw new UnsupportedOperationException(); }
        @Override public void setCharacterStream(int parameterIndex, java.io.Reader reader, int length) { throw new UnsupportedOperationException(); }
        @Override public void setRef(int parameterIndex, java.sql.Ref x) { throw new UnsupportedOperationException(); }
        @Override public void setBlob(int parameterIndex, java.sql.Blob x) { throw new UnsupportedOperationException(); }
        @Override public void setClob(int parameterIndex, java.sql.Clob x) { throw new UnsupportedOperationException(); }
        @Override public void setArray(int parameterIndex, java.sql.Array x) { throw new UnsupportedOperationException(); }
        @Override public java.sql.ResultSet getGeneratedKeys() { throw new UnsupportedOperationException(); }
        @Override public int executeUpdate(String sql) { throw new UnsupportedOperationException(); }
        @Override public int executeUpdate(String sql, int autoGeneratedKeys) { throw new UnsupportedOperationException(); }
        @Override public int executeUpdate(String sql, int[] columnIndexes) { throw new UnsupportedOperationException(); }
        @Override public int executeUpdate(String sql, String[] columnNames) { throw new UnsupportedOperationException(); }
        @Override public boolean execute(String sql) { throw new UnsupportedOperationException(); }
        @Override public boolean execute(String sql, int autoGeneratedKeys) { throw new UnsupportedOperationException(); }
        @Override public boolean execute(String sql, int[] columnIndexes) { throw new UnsupportedOperationException(); }
        @Override public boolean execute(String sql, String[] columnNames) { throw new UnsupportedOperationException(); }
        @Override public void setNString(int parameterIndex, String value) { throw new UnsupportedOperationException(); }
        @Override public void setNCharacterStream(int parameterIndex, java.io.Reader value, long length) { throw new UnsupportedOperationException(); }
        @Override public void setNClob(int parameterIndex, java.sql.NClob value) { throw new UnsupportedOperationException(); }
        @Override public void setClob(int parameterIndex, java.io.Reader reader, long length) { throw new UnsupportedOperationException(); }
        @Override public void setBlob(int parameterIndex, java.io.InputStream inputStream, long length) { throw new UnsupportedOperationException(); }
        @Override public void setNClob(int parameterIndex, java.io.Reader reader, long length) { throw new UnsupportedOperationException(); }
        @Override public void setSQLXML(int parameterIndex, java.sql.SQLXML xmlObject) { throw new UnsupportedOperationException(); }
        @Override public void setAsciiStream(int parameterIndex, java.io.InputStream x, long length) { throw new UnsupportedOperationException(); }
        @Override public void setBinaryStream(int parameterIndex, java.io.InputStream x, long length) { throw new UnsupportedOperationException(); }
        @Override public void setCharacterStream(int parameterIndex, java.io.Reader reader, long length) { throw new UnsupportedOperationException(); }
        @Override public void setAsciiStream(int parameterIndex, java.io.InputStream x) { throw new UnsupportedOperationException(); }
        @Override public void setBinaryStream(int parameterIndex, java.io.InputStream x) { throw new UnsupportedOperationException(); }
        @Override public void setCharacterStream(int parameterIndex, java.io.Reader reader) { throw new UnsupportedOperationException(); }
        @Override public void setNCharacterStream(int parameterIndex, java.io.Reader value) { throw new UnsupportedOperationException(); }
        @Override public void setClob(int parameterIndex, java.io.Reader reader) { throw new UnsupportedOperationException(); }
        @Override public void setBlob(int parameterIndex, java.io.InputStream inputStream) { throw new UnsupportedOperationException(); }
        @Override public void setNClob(int parameterIndex, java.io.Reader reader) { throw new UnsupportedOperationException(); }
        @Override public void close() { }
        @Override public int getMaxFieldSize() { throw new UnsupportedOperationException(); }
        @Override public void setMaxFieldSize(int max) { throw new UnsupportedOperationException(); }
        @Override public int getMaxRows() { throw new UnsupportedOperationException(); }
        @Override public void setMaxRows(int max) { throw new UnsupportedOperationException(); }
        @Override public void setEscapeProcessing(boolean enable) { throw new UnsupportedOperationException(); }
        @Override public int getQueryTimeout() { throw new UnsupportedOperationException(); }
        @Override public void setQueryTimeout(int seconds) { throw new UnsupportedOperationException(); }
        @Override public void cancel() { throw new UnsupportedOperationException(); }
        @Override public java.sql.SQLWarning getWarnings() { throw new UnsupportedOperationException(); }
        @Override public void clearWarnings() { throw new UnsupportedOperationException(); }
        @Override public void setCursorName(String name) { throw new UnsupportedOperationException(); }
        @Override public ResultSet getResultSet() { throw new UnsupportedOperationException(); }
        @Override public int getUpdateCount() { throw new UnsupportedOperationException(); }
        @Override public boolean getMoreResults() { throw new UnsupportedOperationException(); }
        @Override public void setFetchDirection(int direction) { throw new UnsupportedOperationException(); }
        @Override public int getFetchDirection() { throw new UnsupportedOperationException(); }
        @Override public void setFetchSize(int rows) { throw new UnsupportedOperationException(); }
        @Override public int getFetchSize() { throw new UnsupportedOperationException(); }
        @Override public int getResultSetConcurrency() { throw new UnsupportedOperationException(); }
        @Override public int getResultSetType() { throw new UnsupportedOperationException(); }
        @Override public void addBatch(String sql) { throw new UnsupportedOperationException(); }
        @Override public void clearBatch() { throw new UnsupportedOperationException(); }
        @Override public int[] executeBatch() { throw new UnsupportedOperationException(); }
        @Override public Connection getConnection() { throw new UnsupportedOperationException(); }
        @Override public boolean getMoreResults(int current) { throw new UnsupportedOperationException(); }
        @Override public int getResultSetHoldability() { throw new UnsupportedOperationException(); }
        @Override public boolean isClosed() { throw new UnsupportedOperationException(); }
        @Override public void setPoolable(boolean poolable) { throw new UnsupportedOperationException(); }
        @Override public boolean isPoolable() { throw new UnsupportedOperationException(); }
        @Override public void closeOnCompletion() { throw new UnsupportedOperationException(); }
        @Override public boolean isCloseOnCompletion() { throw new UnsupportedOperationException(); }
        @Override public <T> T unwrap(Class<T> iface) { throw new UnsupportedOperationException(); }
        @Override public boolean isWrapperFor(Class<?> iface) { throw new UnsupportedOperationException(); }
    }
    
    private static class TestResultSet implements ResultSet {
        private boolean nextReturns = true;
        private final java.util.Map<String, Integer> intColumns = new java.util.HashMap<>();
        private final java.util.Map<Integer, Integer> indexedIntColumns = new java.util.HashMap<>();
        
        public void setNextReturns(boolean value) {
            this.nextReturns = value;
        }
        
        public void addIntColumn(String name, int value) {
            intColumns.put(name, value);
        }
        
        @Override
        public boolean next() {
            return nextReturns;
        }
        
        @Override
        public int getInt(String columnLabel) {
            return intColumns.getOrDefault(columnLabel, 0);
        }
        
        @Override
        public int getInt(int columnIndex) {
            return indexedIntColumns.getOrDefault(columnIndex, intColumns.getOrDefault("", 0));
        }
        
        // Méthodes non implémentées pour les tests
        @Override public boolean wasNull() { throw new UnsupportedOperationException(); }
        @Override public String getString(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public boolean getBoolean(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public byte getByte(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public short getShort(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public long getLong(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public float getFloat(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public double getDouble(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public java.math.BigDecimal getBigDecimal(int columnIndex, int scale) { throw new UnsupportedOperationException(); }
        @Override public byte[] getBytes(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public java.sql.Date getDate(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public java.sql.Time getTime(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public java.sql.Timestamp getTimestamp(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public java.io.InputStream getAsciiStream(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public java.io.InputStream getUnicodeStream(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public java.io.InputStream getBinaryStream(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public String getString(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public boolean getBoolean(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public byte getByte(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public short getShort(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public long getLong(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public float getFloat(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public double getDouble(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public java.math.BigDecimal getBigDecimal(String columnLabel, int scale) { throw new UnsupportedOperationException(); }
        @Override public byte[] getBytes(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public java.sql.Date getDate(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public java.sql.Time getTime(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public java.sql.Timestamp getTimestamp(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public java.io.InputStream getAsciiStream(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public java.io.InputStream getUnicodeStream(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public java.io.InputStream getBinaryStream(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public java.sql.SQLWarning getWarnings() { throw new UnsupportedOperationException(); }
        @Override public void clearWarnings() { throw new UnsupportedOperationException(); }
        @Override public String getCursorName() { throw new UnsupportedOperationException(); }
        @Override public java.sql.ResultSetMetaData getMetaData() { throw new UnsupportedOperationException(); }
        @Override public Object getObject(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public Object getObject(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public int findColumn(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public java.io.Reader getCharacterStream(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public java.io.Reader getCharacterStream(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public java.math.BigDecimal getBigDecimal(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public java.math.BigDecimal getBigDecimal(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public boolean isBeforeFirst() { throw new UnsupportedOperationException(); }
        @Override public boolean isAfterLast() { throw new UnsupportedOperationException(); }
        @Override public boolean isFirst() { throw new UnsupportedOperationException(); }
        @Override public boolean isLast() { throw new UnsupportedOperationException(); }
        @Override public void beforeFirst() { throw new UnsupportedOperationException(); }
        @Override public void afterLast() { throw new UnsupportedOperationException(); }
        @Override public boolean first() { throw new UnsupportedOperationException(); }
        @Override public boolean last() { throw new UnsupportedOperationException(); }
        @Override public int getRow() { throw new UnsupportedOperationException(); }
        @Override public boolean absolute(int row) { throw new UnsupportedOperationException(); }
        @Override public boolean relative(int rows) { throw new UnsupportedOperationException(); }
        @Override public boolean previous() { throw new UnsupportedOperationException(); }
        @Override public void setFetchDirection(int direction) { throw new UnsupportedOperationException(); }
        @Override public int getFetchDirection() { throw new UnsupportedOperationException(); }
        @Override public void setFetchSize(int rows) { throw new UnsupportedOperationException(); }
        @Override public int getFetchSize() { throw new UnsupportedOperationException(); }
        @Override public int getType() { throw new UnsupportedOperationException(); }
        @Override public int getConcurrency() { throw new UnsupportedOperationException(); }
        @Override public boolean rowUpdated() { throw new UnsupportedOperationException(); }
        @Override public boolean rowInserted() { throw new UnsupportedOperationException(); }
        @Override public boolean rowDeleted() { throw new UnsupportedOperationException(); }
        @Override public void updateNull(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public void updateBoolean(int columnIndex, boolean x) { throw new UnsupportedOperationException(); }
        @Override public void updateByte(int columnIndex, byte x) { throw new UnsupportedOperationException(); }
        @Override public void updateShort(int columnIndex, short x) { throw new UnsupportedOperationException(); }
        @Override public void updateInt(int columnIndex, int x) { throw new UnsupportedOperationException(); }
        @Override public void updateLong(int columnIndex, long x) { throw new UnsupportedOperationException(); }
        @Override public void updateFloat(int columnIndex, float x) { throw new UnsupportedOperationException(); }
        @Override public void updateDouble(int columnIndex, double x) { throw new UnsupportedOperationException(); }
        @Override public void updateBigDecimal(int columnIndex, java.math.BigDecimal x) { throw new UnsupportedOperationException(); }
        @Override public void updateString(int columnIndex, String x) { throw new UnsupportedOperationException(); }
        @Override public void updateBytes(int columnIndex, byte[] x) { throw new UnsupportedOperationException(); }
        @Override public void updateDate(int columnIndex, java.sql.Date x) { throw new UnsupportedOperationException(); }
        @Override public void updateTime(int columnIndex, java.sql.Time x) { throw new UnsupportedOperationException(); }
        @Override public void updateTimestamp(int columnIndex, java.sql.Timestamp x) { throw new UnsupportedOperationException(); }
        @Override public void updateAsciiStream(int columnIndex, java.io.InputStream x, int length) { throw new UnsupportedOperationException(); }
        @Override public void updateBinaryStream(int columnIndex, java.io.InputStream x, int length) { throw new UnsupportedOperationException(); }
        @Override public void updateCharacterStream(int columnIndex, java.io.Reader x, int length) { throw new UnsupportedOperationException(); }
        @Override public void updateObject(int columnIndex, Object x, int scaleOrLength) { throw new UnsupportedOperationException(); }
        @Override public void updateObject(int columnIndex, Object x) { throw new UnsupportedOperationException(); }
        @Override public void updateNull(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public void updateBoolean(String columnLabel, boolean x) { throw new UnsupportedOperationException(); }
        @Override public void updateByte(String columnLabel, byte x) { throw new UnsupportedOperationException(); }
        @Override public void updateShort(String columnLabel, short x) { throw new UnsupportedOperationException(); }
        @Override public void updateInt(String columnLabel, int x) { throw new UnsupportedOperationException(); }
        @Override public void updateLong(String columnLabel, long x) { throw new UnsupportedOperationException(); }
        @Override public void updateFloat(String columnLabel, float x) { throw new UnsupportedOperationException(); }
        @Override public void updateDouble(String columnLabel, double x) { throw new UnsupportedOperationException(); }
        @Override public void updateBigDecimal(String columnLabel, java.math.BigDecimal x) { throw new UnsupportedOperationException(); }
        @Override public void updateString(String columnLabel, String x) { throw new UnsupportedOperationException(); }
        @Override public void updateBytes(String columnLabel, byte[] x) { throw new UnsupportedOperationException(); }
        @Override public void updateDate(String columnLabel, java.sql.Date x) { throw new UnsupportedOperationException(); }
        @Override public void updateTime(String columnLabel, java.sql.Time x) { throw new UnsupportedOperationException(); }
        @Override public void updateTimestamp(String columnLabel, java.sql.Timestamp x) { throw new UnsupportedOperationException(); }
        @Override public void updateAsciiStream(String columnLabel, java.io.InputStream x, int length) { throw new UnsupportedOperationException(); }
        @Override public void updateBinaryStream(String columnLabel, java.io.InputStream x, int length) { throw new UnsupportedOperationException(); }
        @Override public void updateCharacterStream(String columnLabel, java.io.Reader reader, int length) { throw new UnsupportedOperationException(); }
        @Override public void updateObject(String columnLabel, Object x, int scaleOrLength) { throw new UnsupportedOperationException(); }
        @Override public void updateObject(String columnLabel, Object x) { throw new UnsupportedOperationException(); }
        @Override public void insertRow() { throw new UnsupportedOperationException(); }
        @Override public void updateRow() { throw new UnsupportedOperationException(); }
        @Override public void deleteRow() { throw new UnsupportedOperationException(); }
        @Override public void refreshRow() { throw new UnsupportedOperationException(); }
        @Override public void cancelRowUpdates() { throw new UnsupportedOperationException(); }
        @Override public void moveToInsertRow() { throw new UnsupportedOperationException(); }
        @Override public void moveToCurrentRow() { throw new UnsupportedOperationException(); }
        @Override public java.sql.Statement getStatement() { throw new UnsupportedOperationException(); }
        @Override public Object getObject(int columnIndex, java.util.Map<String, Class<?>> map) { throw new UnsupportedOperationException(); }
        @Override public java.sql.Ref getRef(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public java.sql.Blob getBlob(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public java.sql.Clob getClob(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public java.sql.Array getArray(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public Object getObject(String columnLabel, java.util.Map<String, Class<?>> map) { throw new UnsupportedOperationException(); }
        @Override public java.sql.Ref getRef(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public java.sql.Blob getBlob(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public java.sql.Clob getClob(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public java.sql.Array getArray(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public java.sql.Date getDate(int columnIndex, java.util.Calendar cal) { throw new UnsupportedOperationException(); }
        @Override public java.sql.Date getDate(String columnLabel, java.util.Calendar cal) { throw new UnsupportedOperationException(); }
        @Override public java.sql.Time getTime(int columnIndex, java.util.Calendar cal) { throw new UnsupportedOperationException(); }
        @Override public java.sql.Time getTime(String columnLabel, java.util.Calendar cal) { throw new UnsupportedOperationException(); }
        @Override public java.sql.Timestamp getTimestamp(int columnIndex, java.util.Calendar cal) { throw new UnsupportedOperationException(); }
        @Override public java.sql.Timestamp getTimestamp(String columnLabel, java.util.Calendar cal) { throw new UnsupportedOperationException(); }
        @Override public java.net.URL getURL(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public java.net.URL getURL(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public void updateRef(int columnIndex, java.sql.Ref x) { throw new UnsupportedOperationException(); }
        @Override public void updateRef(String columnLabel, java.sql.Ref x) { throw new UnsupportedOperationException(); }
        @Override public void updateBlob(int columnIndex, java.sql.Blob x) { throw new UnsupportedOperationException(); }
        @Override public void updateBlob(String columnLabel, java.sql.Blob x) { throw new UnsupportedOperationException(); }
        @Override public void updateClob(int columnIndex, java.sql.Clob x) { throw new UnsupportedOperationException(); }
        @Override public void updateClob(String columnLabel, java.sql.Clob x) { throw new UnsupportedOperationException(); }
        @Override public void updateArray(int columnIndex, java.sql.Array x) { throw new UnsupportedOperationException(); }
        @Override public void updateArray(String columnLabel, java.sql.Array x) { throw new UnsupportedOperationException(); }
        @Override public java.sql.RowId getRowId(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public java.sql.RowId getRowId(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public void updateRowId(int columnIndex, java.sql.RowId x) { throw new UnsupportedOperationException(); }
        @Override public void updateRowId(String columnLabel, java.sql.RowId x) { throw new UnsupportedOperationException(); }
        @Override public int getHoldability() { throw new UnsupportedOperationException(); }
        @Override public boolean isClosed() { throw new UnsupportedOperationException(); }
        @Override public void updateNString(int columnIndex, String nString) { throw new UnsupportedOperationException(); }
        @Override public void updateNString(String columnLabel, String nString) { throw new UnsupportedOperationException(); }
        @Override public void updateNClob(int columnIndex, java.sql.NClob nClob) { throw new UnsupportedOperationException(); }
        @Override public void updateNClob(String columnLabel, java.sql.NClob nClob) { throw new UnsupportedOperationException(); }
        @Override public java.sql.NClob getNClob(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public java.sql.NClob getNClob(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public java.sql.SQLXML getSQLXML(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public java.sql.SQLXML getSQLXML(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public void updateSQLXML(int columnIndex, java.sql.SQLXML xmlObject) { throw new UnsupportedOperationException(); }
        @Override public void updateSQLXML(String columnLabel, java.sql.SQLXML xmlObject) { throw new UnsupportedOperationException(); }
        @Override public String getNString(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public String getNString(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public java.io.Reader getNCharacterStream(int columnIndex) { throw new UnsupportedOperationException(); }
        @Override public java.io.Reader getNCharacterStream(String columnLabel) { throw new UnsupportedOperationException(); }
        @Override public void updateNCharacterStream(int columnIndex, java.io.Reader x, long length) { throw new UnsupportedOperationException(); }
        @Override public void updateNCharacterStream(String columnLabel, java.io.Reader reader, long length) { throw new UnsupportedOperationException(); }
        @Override public void updateAsciiStream(int columnIndex, java.io.InputStream x, long length) { throw new UnsupportedOperationException(); }
        @Override public void updateBinaryStream(int columnIndex, java.io.InputStream x, long length) { throw new UnsupportedOperationException(); }
        @Override public void updateCharacterStream(int columnIndex, java.io.Reader x, long length) { throw new UnsupportedOperationException(); }
        @Override public void updateAsciiStream(String columnLabel, java.io.InputStream x, long length) { throw new UnsupportedOperationException(); }
        @Override public void updateBinaryStream(String columnLabel, java.io.InputStream x, long length) { throw new UnsupportedOperationException(); }
        @Override public void updateCharacterStream(String columnLabel, java.io.Reader reader, long length) { throw new UnsupportedOperationException(); }
        @Override public void updateBlob(int columnIndex, java.io.InputStream inputStream, long length) { throw new UnsupportedOperationException(); }
        @Override public void updateBlob(String columnLabel, java.io.InputStream inputStream, long length) { throw new UnsupportedOperationException(); }
        @Override public void updateClob(int columnIndex, java.io.Reader reader, long length) { throw new UnsupportedOperationException(); }
        @Override public void updateClob(String columnLabel, java.io.Reader reader, long length) { throw new UnsupportedOperationException(); }
        @Override public void updateNClob(int columnIndex, java.io.Reader reader, long length) { throw new UnsupportedOperationException(); }
        @Override public void updateNClob(String columnLabel, java.io.Reader reader, long length) { throw new UnsupportedOperationException(); }
        @Override public void updateNCharacterStream(int columnIndex, java.io.Reader x) { throw new UnsupportedOperationException(); }
        @Override public void updateNCharacterStream(String columnLabel, java.io.Reader reader) { throw new UnsupportedOperationException(); }
        @Override public void updateAsciiStream(int columnIndex, java.io.InputStream x) { throw new UnsupportedOperationException(); }
        @Override public void updateBinaryStream(int columnIndex, java.io.InputStream x) { throw new UnsupportedOperationException(); }
        @Override public void updateCharacterStream(int columnIndex, java.io.Reader x) { throw new UnsupportedOperationException(); }
        @Override public void updateAsciiStream(String columnLabel, java.io.InputStream x) { throw new UnsupportedOperationException(); }
        @Override public void updateBinaryStream(String columnLabel, java.io.InputStream x) { throw new UnsupportedOperationException(); }
        @Override public void updateCharacterStream(String columnLabel, java.io.Reader reader) { throw new UnsupportedOperationException(); }
        @Override public void updateBlob(int columnIndex, java.io.InputStream inputStream) { throw new UnsupportedOperationException(); }
        @Override public void updateBlob(String columnLabel, java.io.InputStream inputStream) { throw new UnsupportedOperationException(); }
        @Override public void updateClob(int columnIndex, java.io.Reader reader) { throw new UnsupportedOperationException(); }
        @Override public void updateClob(String columnLabel, java.io.Reader reader) { throw new UnsupportedOperationException(); }
        @Override public void updateNClob(int columnIndex, java.io.Reader reader) { throw new UnsupportedOperationException(); }
        @Override public void updateNClob(String columnLabel, java.io.Reader reader) { throw new UnsupportedOperationException(); }
        @Override public <T> T getObject(int columnIndex, Class<T> type) { throw new UnsupportedOperationException(); }
        @Override public <T> T getObject(String columnLabel, Class<T> type) { throw new UnsupportedOperationException(); }
        @Override public <T> T unwrap(Class<T> iface) { throw new UnsupportedOperationException(); }
        @Override public boolean isWrapperFor(Class<?> iface) { throw new UnsupportedOperationException(); }
        @Override public void close() throws SQLException {}
    }
    
    private static class TestJoueurDAOImpl extends JoueurDAOImpl {
        private final List<Joueur> joueurs = new ArrayList<>();
        
        public TestJoueurDAOImpl() {
            // Constructeur pour éviter l'initialisation de JoueurDAOImpl
        }
        
        public void addJoueur(Joueur joueur) {
            joueurs.add(joueur);
        }
        
        @Override
        public Joueur obtenirJoueurParId(int id) {
            return joueurs.stream()
                    .filter(j -> j.getId() == id)
                    .findFirst()
                    .orElse(null);
        }
        
        @Override
        public static JoueurDAOImpl getInstance() {
            return null; // Non utilisé dans les tests
        }
    }
    
    private static class TestRoyaumeMongoDAOImpl extends RoyaumeMongoDAOImpl {
        private final List<Royaume> royaumes = new ArrayList<>();
        
        public TestRoyaumeMongoDAOImpl() {
            // Constructeur pour éviter l'initialisation de RoyaumeMongoDAOImpl
        }
        
        public void addRoyaume(Royaume royaume) {
            royaumes.add(royaume);
        }
        
        @Override
        public Royaume getRoyaumeByJoueurId(int joueurId) {
            return royaumes.stream()
                    .filter(r -> r.getId() == joueurId) // Simplifié pour les tests
                    .findFirst()
                    .orElse(null);
        }
    }
} 