package ca.tnt.ldaputils.proprietary;

import ca.tnt.ldaputils.ILdapOrganization;

/**
 * Implements the tntbusiness LDAP objectClass.
 * <p/>
 * Created :  14-Apr-2006 9:24:54 PM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 *
 * @author Trenton D. Adams <trenta@athabascau.ca>
 */
public interface ILdapBusiness extends ILdapOrganization
{
    public String getBusinessContact();

    public String[] getLabeledURI();

    public String getMail();

    public void setBusinessContact(String businessContact, int operation);

    public void setLabeledURI(String labeledURI, int operation);

    public void setMail(String mail, int operation);

    /**
     * @return An array of all email addresses
     */
    String[] getMails();
}
