package be.helha.projects.GuerreDesRoyaumes.Service;

import java.util.List;
import java.util.Map;

public interface ClassementService {

    List<Map<String, Object>> getClassementVictoiresDefaites();
    
    List<Map<String, Object>> getClassementRichesse();
    
    List<Map> getClassementNiveauRoyaumes();
} 