package ca.tnt.ldaputils.exception;

import javax.naming.NamingException;

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * Created :  27/11/14 7:51 PM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 *
 * @author Trenton D. Adams
 */
public class LpaMissingRequiredClassesException extends LpaAnnotationException
{
    public LpaMissingRequiredClassesException(final Throwable namingException)
    {
        super(namingException);
    }

    public LpaMissingRequiredClassesException(
        final NamingException namingException)
    {
        super(namingException);
    }

    public LpaMissingRequiredClassesException(final String userMessage,
        final Throwable namingException)
    {
        super(userMessage, namingException);
    }

    public LpaMissingRequiredClassesException(final String message)
    {
        super(message);
    }
}
