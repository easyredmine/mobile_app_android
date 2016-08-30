package cz.ackee.androidskeleton.model.base;

import cz.ackee.androidskeleton.model.IssueHash;

/**
 * TODO add class description
 *
 * @author Michal Kučera [michal.kucera@ackee.cz]
 * @since 17/12/15
 **/
public class IssueHashFieldsSerializer extends IssueHashSerializer {
    public static final String TAG = IssueHashFieldsSerializer.class.getName();


    @Override
    protected boolean shouldAddFixedVersionAllTime(IssueHash src) {
        return src.alwaysSendMilestone;
    }

    @Override
    protected boolean shouldAddCategoryAllTime(IssueHash src) {
        return src.alwaysSendMilestone;
    }
}
