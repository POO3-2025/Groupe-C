package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Competence;
import java.util.List;

public interface CompetenceDAO {
    void addCompetence(Competence competence);
    Competence getCompetenceById(int id);
    List<Competence> getAllCompetences();
    void updateCompetence(Competence competence);
    void deleteCompetence(int id);
}
