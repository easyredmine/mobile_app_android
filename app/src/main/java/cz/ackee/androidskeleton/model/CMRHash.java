package cz.ackee.androidskeleton.model;

/**
 * Original project name : easyredmine-android-aplikace
 * Created by Petr Lorenc[petr.lorenc@ackee.cz] on 11.6.2015.
 */
public class CMRHash {
    public String name;
    public String email;
    public String telephone;

    public CMRHash(String name, String email, String telephone) {
        this.name = name;
        this.email = email;
        this.telephone = telephone;
    }

    @Override
    public String toString() {
        return "CMRHash{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }
}
