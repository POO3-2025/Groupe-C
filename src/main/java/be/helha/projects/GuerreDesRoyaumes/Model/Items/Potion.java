package be.helha.projects.GuerreDesRoyaumes.Model.Items;


import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

public class Potion extends Item {

    private double degats;
    private double soin;

    //Constructeur
    public Potion(int id, String nom, int quantiteMax, int prix, double degats, double soin) {
        super(id, nom, quantiteMax, "Potion", prix);
        this.degats = degats;
        this.soin = soin;
    }


    //Getteur
    public double getDegats() {
        return degats;
    }

    public double getSoin() {
        return soin;
    }

    //Setteur
    public void setDegats(double degats) {
        this.degats = degats;
    }

    public void setSoin(double soin) {
        this.soin = soin;
    }



    @Override
    public void use(Personnage personnage) {
        // Si la potion inflige des dégâts
        if (degats > 0) {
            double viePersonnage = personnage.getVie();
            double nouvelleVie = viePersonnage - degats;
            personnage.setVie(nouvelleVie);
            System.out.println(personnage.getNom() + " subit " + degats + " dégâts de la potion. Vie restante : " + personnage.getVie());
        }

        // Si la potion soigne
        if (soin > 0) {
            double viePersonnage = personnage.getVie();
            double nouvelleVie = viePersonnage + soin;
            personnage.setVie(nouvelleVie);
            System.out.println(personnage.getNom() + " récupère " + soin + " points de vie grâce à la potion. Vie actuelle : " + personnage.getVie());
        }
    }

//    @Override
//    public String toString() {
//        String effet = "Effet : ";
//        if (degats > 0) {
//            effet += "Inflige " + degats + " dégâts.";
//        }
//        if (soin > 0) {
//            if (degats > 0) {
//                effet += " | ";
//            }
//            effet += "Soigne " + soin + " points de vie.";
//        }
//
//        return super.toString() + "\n" + effet;
//    }
}
