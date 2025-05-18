package be.helha.projects.GuerreDesRoyaumes.Controller;

import be.helha.projects.GuerreDesRoyaumes.Service.ClassementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/classements")
public class ClassementController {
    
    @Autowired
    private ClassementService classementService;
    
    @GetMapping("")
    public ModelAndView afficherClassements() {
        ModelAndView modelAndView = new ModelAndView("classements");
        
        // Récupération des classements via le service
        List<Map<String, Object>> classementVD = classementService.getClassementVictoiresDefaites();
        List<Map<String, Object>> classementRichesse = classementService.getClassementRichesse();
        List<Map> classementRoyaumes = classementService.getClassementNiveauRoyaumes();
        
        modelAndView.addObject("classementVD", classementVD);
        modelAndView.addObject("classementRichesse", classementRichesse);
        modelAndView.addObject("classementRoyaumes", classementRoyaumes);
        
        return modelAndView;
    }
} 