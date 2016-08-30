package cz.ackee.androidskeleton.utils;

/**
 * Created by petr.schneider@ackee.cz on 13.3.2015.
 */
public enum AccountType {
    EASY_REDMINE(0), REDMINE(1);

    public int value;

    AccountType(int i) {
        this.value = i;
    }
}
