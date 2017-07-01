package isaias.santana.firebasepaginator;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * @author Isaías Santana on 01/07/17.
 *         email: isds.santana@gmail.com
 */

@IgnoreExtraProperties
public class SampleModel
{
    private String userName;

    public SampleModel(){}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
