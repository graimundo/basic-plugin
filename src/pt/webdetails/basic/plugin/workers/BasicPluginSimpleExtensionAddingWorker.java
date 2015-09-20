/*!
* Copyright 2002 - 2015 Webdetails, a Pentaho company.  All rights reserved.
*
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/
package pt.webdetails.basic.plugin.workers;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.mimetype.IMimeType;
import org.pentaho.platform.api.mimetype.IPlatformMimeResolver;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.plugin.services.importer.IPlatformImportHandler;
import org.pentaho.platform.plugin.services.importer.IPlatformImporter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class allows a plugin to programmatically add new file extensions to an *already existing* platform mimeType
 * declaration;
 */
public class BasicPluginSimpleExtensionAddingWorker implements IBasicPluginWorker {

  protected static Log logger = LogFactory.getLog( BasicPluginSimpleExtensionAddingWorker.class );

  private IPlatformImporter importer;
  private IPlatformMimeResolver mimeResolver;
  private List<IMimeType> basicPluginMimeTypes;

  public BasicPluginSimpleExtensionAddingWorker( List<IMimeType> basicPluginMimeTypes ) {
    setBasicPluginMimeTypes( basicPluginMimeTypes != null ? basicPluginMimeTypes : new ArrayList<IMimeType>() );
  }

  @Override
  public void work() {

    setImporter( PentahoSystem.get( IPlatformImporter.class ) );
    setMimeResolver( PentahoSystem.get( IPlatformMimeResolver.class ) );

    for( IMimeType basicPluginMimeType : getBasicPluginMimeTypes() ){

      addExtensionsToPlatformMimeResolver( basicPluginMimeType );

      addExtensionToExistingImporterHandler( basicPluginMimeType );
    }

  }

  protected void addExtensionsToPlatformMimeResolver( IMimeType basicPluginMimeType ) {

    if ( basicPluginMimeType != null && getMimeResolver() != null ) {

      for( String extension : basicPluginMimeType.getExtensions() ){

        if( getMimeResolver().resolveMimeTypeForFileName( "." + extension.toLowerCase() ) == null ){

          getMimeResolver().addMimeType( basicPluginMimeType );
          logger.info( "Added '" + StringUtils.join( basicPluginMimeType.getExtensions(), "," ) + "' with mimeType '"
              + basicPluginMimeType.getName() + "' to IPlatformMimeResolver " );
        }
      }
    }
  }

  protected void addExtensionToExistingImporterHandler( IMimeType basicPluginMimeType ) {

    if( basicPluginMimeType != null && getImporter() != null && getImporter().getHandlers() != null
        && getImporter().getHandlers().get( basicPluginMimeType.getName() ) != null ){

      IPlatformImportHandler handler = getImporter().getHandlers().get( basicPluginMimeType.getName() );

      logger.info( "Found a IPlatformImportHandler '" + handler.getClass().getSimpleName()
          + "' for mimeType '" + basicPluginMimeType.getName() + "'" );

      /*
       * The current implementation of IMimeType ( org.pentaho.platform.core.mimetype.MimeType ) overrides the
       * equals() and hash() methods, and equals is calculated by a mimeType.getName() comparison;
       *
       * *However*, IMimeType does not state that this is the contractual equals() logic, so future/alternate IMimeType
       * implementation classes may opt by a different equals logic.
       */
      int idx = indexOfMimeType( handler.getMimeTypes(), basicPluginMimeType.getName() );

      if( idx != -1 ) {
        handler.getMimeTypes().get( idx ).getExtensions().addAll( basicPluginMimeType.getExtensions() );

        logger.info( "Added '" + StringUtils.join( basicPluginMimeType.getExtensions(), "," ) + "' to mimeType '"
            + basicPluginMimeType.getName() + "' in IPlatformImportHandler '" + handler.getClass().getSimpleName() + "'" );
      }
    }
  }

  private int indexOfMimeType( List<IMimeType> mimeTypes, String mimeTypeName ) {

    for( int i = 0; i < mimeTypes.size() ; i++ ){
      if( mimeTypeName.equalsIgnoreCase( mimeTypes.get( i ).getName() ) ){
        return i;
      }
    }
    return -1; /* none found */
  }

  public IPlatformImporter getImporter() {
    return importer;
  }

  public void setImporter( IPlatformImporter importer ) {
    this.importer = importer;
  }

  public IPlatformMimeResolver getMimeResolver() {
    return mimeResolver;
  }

  public void setMimeResolver( IPlatformMimeResolver mimeResolver ) {
    this.mimeResolver = mimeResolver;
  }

  public List<IMimeType> getBasicPluginMimeTypes() {
    return basicPluginMimeTypes;
  }

  public void setBasicPluginMimeTypes( List<IMimeType> basicPluginMimeTypes ) {
    this.basicPluginMimeTypes = basicPluginMimeTypes;
  }
}
