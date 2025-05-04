package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

import be.helha.projects.GuerreDesRoyaumes.Service.CompetenceService;
import be.helha.projects.GuerreDesRoyaumes.DAO.CompetenceDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.Competence;
import java.util.List;

public class CompetenceServiceImpl implements CompetenceService {

    private CompetenceDAO competenceDAO;

    public CompetenceServiceImpl(CompetenceDAO competenceDAO) {
        this.competenceDAO = competenceDAO;
    }

    @Override
    public void addCompetence(Competence competence) {
        competenceDAO.addCompetence(competence);
    }

    @Override
    public Competence getCompetenceById(int id) {
        return competenceDAO.getCompetenceById(id);
    }

    @Override
    public List<Competence> getAllCompetences() {
        return competenceDAO.getAllCompetences();
    }

    @Override
    public void updateCompetence(Competence competence) {
        competenceDAO.updateCompetence(competence);
    }

    @Override
    public void deleteCompetence(int id) {
        competenceDAO.deleteCompetence(id);
    }
}
