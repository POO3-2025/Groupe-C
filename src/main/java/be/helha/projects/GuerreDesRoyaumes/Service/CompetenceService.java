package be.helha.projects.GuerreDesRoyaumes.Service;

import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.Competence;
import java.util.List;

public interface CompetenceService {
    void addCompetence(Competence competence);
    Competence getCompetenceById(int id);
    List<Competence> getAllCompetences();
    void updateCompetence(Competence competence);
    void deleteCompetence(int id);
}
