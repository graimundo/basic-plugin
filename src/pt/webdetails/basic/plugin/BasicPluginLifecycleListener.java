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
package pt.webdetails.basic.plugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IPlatformReadyListener;
import org.pentaho.platform.api.engine.IPluginLifecycleListener;
import org.pentaho.platform.api.engine.PluginLifecycleException;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import pt.webdetails.basic.plugin.workers.IBasicPluginWorker;

import java.util.List;

public class BasicPluginLifecycleListener implements IPluginLifecycleListener, IPlatformReadyListener {

  protected static Log logger = LogFactory.getLog( BasicPluginLifecycleListener.class );

  @Override
  public void init() throws PluginLifecycleException {
    logger.info( "init()" );
  }

  @Override
  public void loaded() throws PluginLifecycleException {
    logger.info( "loaded()" );
  }

  @Override
  public void unLoaded() throws PluginLifecycleException {
    logger.info( "unloaded()" );
  }

  @Override
  public void ready() throws PluginLifecycleException {
    logger.info( "ready()" );

    /* all platform beans are now up-and-running, so let's go ahead and do some plugin logic */

    List<IBasicPluginWorker> workers = PentahoSystem.getAll( IBasicPluginWorker.class );

    if( workers != null ){

      for( IBasicPluginWorker worker : workers ) {
        worker.work();
      }
    }
  }
}
