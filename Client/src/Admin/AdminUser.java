package Admin;

import Util.Config;
import Util.User;

public class AdminUser extends User {

    public AdminUser() {
        super(Config.ADMIN, true);
    }
    public AdminUser(int maxCommand) {
        super(Config.ADMIN, true, maxCommand);
    }

    public int getMaxCommand() {
        return super.getAdminMaxCommand();
    }

    public void setMaxCommand(int maxCommand) {
        super.setAdminMaxCommand(maxCommand);
    }



}