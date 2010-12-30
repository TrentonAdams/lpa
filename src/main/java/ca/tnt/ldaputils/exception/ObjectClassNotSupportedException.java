package ca.tnt.ldaputils.exception;

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * Created :  14-Apr-2006 8:17:54 PM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 *
 * @author trenta
 */
public class ObjectClassNotSupportedException extends LdapNamingException
{
    public ObjectClassNotSupportedException(final String message)
    {
        super(message);
    }
}
