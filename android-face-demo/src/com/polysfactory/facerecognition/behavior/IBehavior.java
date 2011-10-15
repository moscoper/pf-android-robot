package com.polysfactory.facerecognition.behavior;

/**
 * 振る舞いパターンの基底インターフェース<br>
 * @author $Author$
 * @version $Revision$
 */
public interface IBehavior {
    public void action();

    public boolean isFinished();

    public void reset();
}
