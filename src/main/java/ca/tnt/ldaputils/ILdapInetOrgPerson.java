package ca.tnt.ldaputils;

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * Created :  14-Apr-2006 9:20:40 PM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 *
 * @author trenta
 */
public interface ILdapInetOrgPerson extends ILdapEntry
{
    public String getDisplayName();
    public String getCarLicense();
    public String getDepartmentNumber();
    public String getEmployeeNumber();
}
