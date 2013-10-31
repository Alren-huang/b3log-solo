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
package org.b3log.solo.repository.impl;


import org.b3log.latke.Latkes;
import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.repository.StatisticRepository;
import org.b3log.solo.service.StatisticMgmtService;
import org.json.JSONObject;


/**
 * Statistic repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, May 15, 2013
 * @since 0.3.1
 */
@Repository
public final class StatisticRepositoryImpl extends AbstractRepository implements StatisticRepository {

    /**
     * Public constructor.
     */
    public StatisticRepositoryImpl() {
        super(Statistic.STATISTIC);
    }

    @Override
    public void update(final String id, final JSONObject jsonObject) throws RepositoryException {
        super.update(id, jsonObject);

        if (Latkes.isDataCacheEnabled()) {
            getCache().put(StatisticMgmtService.REPOSITORY_CACHE_KEY_PREFIX + Statistic.STATISTIC, jsonObject);
        }
    }
}
