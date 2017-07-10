package isaias.santana.firebasepaginator.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

/**
 * Sample model
 * @author Isaías Santana on 10/07/17.
 *         email: isds.santana@gmail.com
 */

public final class Contact
{
    private  String contactName;
    private  String phoneNumber;

    public Contact(String contactName, String phoneNumber)
    {
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
    }

    public Contact(){}

    public String getContactName() { return contactName; }

    public String getPhoneNumber() { return phoneNumber; }


    @Exclude
    public HashMap<String, Object> toMap()
    {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("contactName",contactName);
        map.put("phoneNumber",phoneNumber);

        return map;
    }
}
