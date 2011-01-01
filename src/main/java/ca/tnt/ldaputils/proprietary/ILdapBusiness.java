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
