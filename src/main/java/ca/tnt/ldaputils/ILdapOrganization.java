package ca.tnt.ldaputils;

import ca.tnt.ldaputils.impl.LdapGroup;

import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * LDAPOrganization represents an object with a class of "organization"
 * <p/>
 * Created :  15-Apr-2006 12:48:52 PM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 * <p/>
 * @author Trenton D. Adams <trenta@athabascau.ca>
 */
public interface ILdapOrganization extends ILdapEntry
{
    /**
     * Retrieves a Map of categories where the key is the category name, and the
     * value is the ILdapGroup.  This allows iteration through the keys, just
     * for the category names, or iteration through the values for the values,
     * or retrieving values by key
     *
     * @return the Map of categories as described
     */
    public SortedMap<String, ? extends ILdapGroup> getBusinessCategories();

    public String getTelephoneNumber();

    public String getFacsimileTelephoneNumber();

    public String getStreet();

    public String getPostOfficeBox();

    public String getPostalCode();

    public String getPostalAddress();

    public String getLocality();

    public String getOrganization();

    public void setBusinessCategories(String[] categories, int operation);

    public void setTelephoneNumber(String telephoneNumber, int operation);

    public void setFacsimileTelephoneNumber(String fax, int operation);

    public void setStreet(String street, int operation);

    public void setPostOfficeBox(String postOfficeBox, int operation);

    public void setPostalCode(String postalCode, int operation);

    public void setPostalAddress(String postalAddress, int operation);

    public void setLocality(String city, int operation);

    public void setOrganization(String organizationName, int operation);

}
