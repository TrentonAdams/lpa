package ca.tnt.ldaputils.exception;

import javax.naming.NamingException;

/**
 * Used when something is wrong with the use of annotations.
 * <p/>
 * Created :  5-Dec-2010 7:15:24 PM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 *
 * @author Trenton D. Adams
 */
@SuppressWarnings({"JavaDoc"})
public class LpaAnnotationException extends LdapNamingException
{
    public LpaAnnotationException(final Throwable namingException)
    {
        super(namingException);
    }

    public LpaAnnotationException(final NamingException namingException)
    {
        super(namingException);
    }

    public LpaAnnotationException(final String userMessage, final Throwable namingException)
    {
        super(userMessage, namingException);
    }

    public LpaAnnotationException(final String message)
    {
        super(message);
    }
}
