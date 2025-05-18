package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;

import java.util.Arrays;

/**
 * Un composant pour afficher plusieurs lignes de texte.
 * Cette classe est utilisée pour afficher des messages multi-lignes dans l'interface utilisateur.
 */
public class MultiLineLabel extends Panel {
    
    /**
     * Crée un nouveau MultiLineLabel avec le texte fourni.
     * 
     * @param text Le texte à afficher, peut contenir des sauts de ligne '\n'
     */
    public MultiLineLabel(String text) {
        super(new GridLayout(1));
        
        if (text == null || text.isEmpty()) {
            addComponent(new Label(""));
            return;
        }
        
        // Diviser le texte en lignes
        String[] lines = text.split("\n");
        
        // Ajouter chaque ligne comme un Label
        for (String line : lines) {
            addComponent(new Label(line));
        }
    }
    
    /**
     * Crée un nouveau MultiLineLabel avec le texte fourni et la largeur maximale.
     * Si une ligne est plus longue que la largeur maximale, elle sera découpée.
     * 
     * @param text Le texte à afficher
     * @param maxWidth La largeur maximale en caractères
     */
    public MultiLineLabel(String text, int maxWidth) {
        super(new GridLayout(1));
        
        if (text == null || text.isEmpty()) {
            addComponent(new Label(""));
            return;
        }
        
        // Diviser le texte en lignes
        String[] lines = text.split("\n");
        
        // Traiter chaque ligne
        for (String line : lines) {
            if (line.length() <= maxWidth) {
                addComponent(new Label(line));
            } else {
                // Découper les lignes trop longues
                int startPos = 0;
                while (startPos < line.length()) {
                    int endPos = Math.min(startPos + maxWidth, line.length());
                    String segment = line.substring(startPos, endPos);
                    addComponent(new Label(segment));
                    startPos = endPos;
                }
            }
        }
    }
    
    /**
     * Définit le texte du MultiLineLabel.
     * 
     * @param text Le nouveau texte à afficher
     */
    public void setText(String text) {
        // Supprimer tous les composants existants
        removeAllComponents();
        
        if (text == null || text.isEmpty()) {
            addComponent(new Label(""));
            return;
        }
        
        // Diviser le texte en lignes
        String[] lines = text.split("\n");
        
        // Ajouter chaque ligne comme un Label
        for (String line : lines) {
            addComponent(new Label(line));
        }
    }
} 