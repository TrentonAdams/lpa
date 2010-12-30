package ca.tnt.ldaputils.exception;

import javax.naming.NamingException;

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * Created :  Mar 20, 2008 7:41:10 PM MST
 * <p/>
 * Modified : $Date$
 * <p/>
 * Revision : $Revision$
 *
 * @author trenta
 */
public class LdapNamingException extends RuntimeException
{
    public LdapNamingException(final Throwable namingException)
    {
        super(namingException);
    }

    public LdapNamingException(final NamingException namingException)
    {
        super(namingException.getExplanation(), namingException);
    }

    public LdapNamingException(final String userMessage, final Throwable namingException)
    {
        super(namingException);
    }

    public LdapNamingException(final String message)
    {
        super(message);
    }
}
