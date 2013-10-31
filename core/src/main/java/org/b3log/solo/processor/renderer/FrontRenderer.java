/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.solo.processor.renderer;


import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.cache.PageCaches;
import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.renderer.freemarker.CacheFreeMarkerRenderer;
import org.b3log.solo.model.Common;
import org.b3log.solo.processor.util.TopBars;
import org.b3log.solo.service.StatisticMgmtService;


/**
 * <a href="http://freemarker.org">FreeMarker</a> HTTP response 
 * renderer.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.7, Jul 16, 2012
 * @since 0.3.1
 */
public final class FrontRenderer extends CacheFreeMarkerRenderer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(FrontRenderer.class.getName());

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Puts the top bar replacement flag into data model.
     * </p>
     */
    @Override
    protected void beforeRender(final HTTPRequestContext context) throws Exception {
        LOGGER.log(Level.TRACE, "Before render....");
        getDataModel().put(Common.TOP_BAR_REPLACEMENT_FLAG_KEY, Common.TOP_BAR_REPLACEMENT_FLAG);
    }

    @Override
    protected void doRender(final String html, final HttpServletRequest request, final HttpServletResponse response)
        throws Exception {
        LOGGER.log(Level.TRACE, "Do render....");
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        PrintWriter writer;

        try {
            writer = response.getWriter();
        } catch (final Exception e) {
            writer = new PrintWriter(response.getOutputStream());
        }

        if (response.isCommitted()) { // response has been sent redirect
            writer.flush();
            writer.close();

            return;
        }

        final String pageContent = (String) request.getAttribute(PageCaches.CACHED_CONTENT);
        String output = html;

        final LatkeBeanManager beanManager = Lifecycle.getBeanManager();
        
        if (null != pageContent) {
            final TopBars topbars = beanManager.getReference(TopBars.class);
            // Adds the top bar HTML content for output
            final String topBarHTML = topbars.getTopBarHTML(request, response);

            output = html.replace(Common.TOP_BAR_REPLACEMENT_FLAG, topBarHTML);
        }

        final StatisticMgmtService statisticMgmtService = beanManager.getReference(StatisticMgmtService.class);
        
        // Inc blog view count
        try {
            statisticMgmtService.incBlogViewCount(request, response);
        } catch (final Exception e) {
            LOGGER.log(Level.WARN, "Incs blog view count failed", e);
        }

        // Write out
        writer.write(output);
        writer.flush();
        writer.close();
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Skips page caching if requested by mobile device.
     * </p>
     */
    @Override
    protected void afterRender(final HTTPRequestContext context) throws Exception {
        LOGGER.log(Level.TRACE, "After render....");

        final HttpServletRequest request = context.getRequest();

        if ("mobile".equals((String) request.getAttribute(Keys.TEMAPLTE_DIR_NAME))) {
            // Skips page caching if requested by mobile device
            return;
        }

        super.afterRender(context);
    }
}
