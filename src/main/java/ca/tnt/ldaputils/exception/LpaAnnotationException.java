/**
 * This file is part of the Ldap Persistence API (LPA).
 * 
 * Copyright Trenton D. Adams <lpa at trentonadams daught ca>
 * 
 * LPA is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * LPA is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with LPA.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * See the COPYING file for more information.
 */
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
