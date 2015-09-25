package de.wdilab.coma.gui.extensions.semantica;

/**
 * Created with IntelliJ IDEA.
 * User: Evmorfia
 * Date: 30/6/2014
 * Time: 2:52 μμ
 * To change this template use File | Settings | File Templates.
 */
public class SASresponse {

    private String message;
    private boolean success;

    public SASresponse(String msg, boolean succ){
        message=msg;
        success=succ;
    }

    public SASresponse(boolean succ){
        message="empty";
        success=succ;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
