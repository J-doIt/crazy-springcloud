package com.crazymaker.springcloud.base.filter;

/*
 * Copyright 2014-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import com.crazymaker.springcloud.common.context.SessionHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.util.OnCommittedResponseWrapper;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;


/**
 * Switches the {@link javax.servlet.http.HttpSession} implementation to be backed by a
 * {@link org.springframework.session.Session}.
 * <p>
 * The {@link CustomedSessionRepositoryFilter} wraps the
 * {@link javax.servlet.http.HttpServletRequest} and overrides the methods to getStr an
 * {@link javax.servlet.http.HttpSession} to be backed by a
 * {@link org.springframework.session.Session} returned by the
 * {@link org.springframework.session.SessionRepository}.
 * <p>
 * The {@link CustomedSessionRepositoryFilter} uses a {@link HttpSessionIdResolver} (default
 * {@link CookieHttpSessionIdResolver}) to bridge logic between an
 * {@link javax.servlet.http.HttpSession} and the
 * {@link org.springframework.session.Session} abstraction. Specifically:
 *
 * <ul>
 * <li>The session id is looked up using
 * {@link HttpSessionIdResolver#resolveSessionIds(javax.servlet.http.HttpServletRequest)}
 * . The default is to look in a cookie named SESSION.</li>
 * <li>The session id of newly created {@link org.springframework.session.Session} is sent
 * to the client using
 * <li>The client is notified that the session id is no longer valid with
 * {@link HttpSessionIdResolver#expireSession(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
 * </li>
 * </ul>
 *
 * <p>
 * The CustomedSessionRepositoryFilter must be placed before any Filter that access the
 * HttpSession or that might commit the response to ensure the session is overridden and
 * persisted properly.
 * </p>
 *
 * @param <S> the {@link Session} type.
 * @author Rob Winch
 * @author Vedran Pavic
 * @author Josh Cummings
 * @since 1.0
 */
public class CustomedSessionRepositoryFilter<S extends Session> extends OncePerRequestFilter
{

    private static final String SESSION_LOGGER_NAME =
            CustomedSessionRepositoryFilter.class.getName().concat(".SESSION_LOGGER");

    private static final Log SESSION_LOGGER =
            LogFactory.getLog(SESSION_LOGGER_NAME);

    /**
     * The session repository request attribute nickname.
     */
    public static final String SESSION_REPOSITORY_ATTR =
            SessionRepository.class.getName();

    /**
     * Invalid session id (not backed by the session repository) request attribute nickname.
     */
    public static final String INVALID_SESSION_ID_ATTR =
            SESSION_REPOSITORY_ATTR + ".invalidSessionId";

    private static final String CURRENT_SESSION_ATTR =
            SESSION_REPOSITORY_ATTR + ".CURRENT_SESSION";

    /**
     * The default filter order.
     */
    public static final int DEFAULT_ORDER = Integer.MIN_VALUE + 50;

    private final SessionRepository<S> sessionRepository;

    private ServletContext servletContext;

    private HttpSessionIdResolver httpSessionIdResolver =
            new CookieHttpSessionIdResolver();

    /**
     * Creates a new instance.
     *
     * @param sessionRepository the <code>SessionRepository</code> to use. Cannot be null.
     */
    public CustomedSessionRepositoryFilter(SessionRepository sessionRepository)
    {
        if (sessionRepository == null)
        {
            throw new IllegalArgumentException("sessionRepository cannot be null");
        }
        this.sessionRepository = sessionRepository;
    }

    /**
     * Sets the {@link HttpSessionIdResolver} to be used. The default is a
     * {@link CookieHttpSessionIdResolver}.
     *
     * @param httpSessionIdResolver the {@link HttpSessionIdResolver} to use. Cannot be
     *                              null.
     */
    public void setHttpSessionIdResolver(HttpSessionIdResolver httpSessionIdResolver)
    {
        if (httpSessionIdResolver == null)
        {
            throw new IllegalArgumentException("httpSessionIdResolver cannot be null");
        }
        this.httpSessionIdResolver = httpSessionIdResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException
    {
        request.setAttribute(SESSION_REPOSITORY_ATTR,
                this.sessionRepository);

        if (this.servletContext == null)
        {
            this.servletContext = request.getServletContext();
        }

        // 包装原始HttpServletRequest至SessionRepositoryRequestWrapper

        SessionRepositoryRequestWrapper wrappedRequest =
                new SessionRepositoryRequestWrapper(request, response, this.servletContext);
        // 包装原始HttpServletResponse响应至SessionRepositoryResponseWrapper
        SessionRepositoryResponseWrapper wrappedResponse =
                new SessionRepositoryResponseWrapper(wrappedRequest, response);

        try
        {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally
        {
            wrappedRequest.commitSession();
        }
    }
    /**
     * 返回true代表不执行过滤器，false代表执行
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return true;
    }

    /**
     * 返回true代表不执行过滤器，false代表执行
     */
    protected boolean shouldNotFilterOld(HttpServletRequest request)
    {
        //如果 请求中携带了 session 的身份标识
        if (null == SessionHolder.getUserIdentifier())
        {
            return true;
        }

        return false;
    }

    public void setServletContext(ServletContext servletContext)
    {
        this.servletContext = servletContext;
    }

    /**
     * Allows ensuring that the session is saved if the response is committed.
     *
     * @author Rob Winch
     * @since 1.0
     */
    private final class SessionRepositoryResponseWrapper
            extends OnCommittedResponseWrapper
    {

        private final CustomedSessionRepositoryFilter.SessionRepositoryRequestWrapper request;

        /**
         * Create a new {@link CustomedSessionRepositoryFilter.SessionRepositoryResponseWrapper}.
         *
         * @param request  the request to be wrapped
         * @param response the response to be wrapped
         */
        SessionRepositoryResponseWrapper(
                CustomedSessionRepositoryFilter.SessionRepositoryRequestWrapper request,
                HttpServletResponse response)
        {
            super(response);
            if (request == null)
            {
                throw new IllegalArgumentException("request cannot be null");
            }
            this.request = request;
        }

        @Override
        protected void onResponseCommitted()
        {
            this.request.commitSession();
        }
    }

    /**
     * A {@link javax.servlet.http.HttpServletRequest} that retrieves the
     * {@link javax.servlet.http.HttpSession} using a
     * {@link org.springframework.session.SessionRepository}.
     *
     * @author Rob Winch
     * @since 1.0
     */
    private final class SessionRepositoryRequestWrapper
            extends HttpServletRequestWrapper
    {

        private final HttpServletResponse response;

        private final ServletContext servletContext;

        private S requestedSession;

        private boolean requestedSessionCached;

        private String requestedSessionId;

        private Boolean requestedSessionIdValid;

        private boolean requestedSessionInvalidated;

        private SessionRepositoryRequestWrapper(HttpServletRequest request,
                                                HttpServletResponse response, ServletContext servletContext)
        {
            super(request);
            this.response = response;
            this.servletContext = servletContext;
        }

        /**
         * Uses the {@link HttpSessionIdResolver} to write the session id to the response
         * and persist the Session.
         */
        private void commitSession()
        {
            HttpSessionWrapper wrappedSession = getCurrentSession();
            if (wrappedSession == null)
            {
                if (isInvalidateClientSession())
                {
                    CustomedSessionRepositoryFilter.this
                            .httpSessionIdResolver.expireSession(this, this.response);
                }
            } else
            {
                S session = wrappedSession.getSession();
                clearRequestedSessionCache();
                CustomedSessionRepositoryFilter.this.sessionRepository.save(session);
                String sessionId = session.getId();
                if (!isRequestedSessionIdValid()
                        || !sessionId.equals(getRequestedSessionId()))
                {
                    CustomedSessionRepositoryFilter.this.httpSessionIdResolver.setSessionId(this,
                            this.response, sessionId);
                }
            }
        }

        @SuppressWarnings("unchecked")
        private CustomedSessionRepositoryFilter.SessionRepositoryRequestWrapper.HttpSessionWrapper getCurrentSession()
        {
            return (CustomedSessionRepositoryFilter.SessionRepositoryRequestWrapper.HttpSessionWrapper) getAttribute(CURRENT_SESSION_ATTR);
        }

        private void setCurrentSession(CustomedSessionRepositoryFilter.SessionRepositoryRequestWrapper.HttpSessionWrapper currentSession)
        {
            if (currentSession == null)
            {
                removeAttribute(CURRENT_SESSION_ATTR);
            } else
            {
                setAttribute(CURRENT_SESSION_ATTR, currentSession);
            }
        }

        @Override
        @SuppressWarnings("unused")
        public String changeSessionId()
        {
            HttpSession session = getSession(false);

            if (session == null)
            {
                throw new IllegalStateException(
                        "Cannot change session ID. There is no session associated with this request.");
            }

            return getCurrentSession().getSession().changeSessionId();
        }

        @Override
        public boolean isRequestedSessionIdValid()
        {
            if (this.requestedSessionIdValid == null)
            {
                S requestedSession = getRequestedSession();
                if (requestedSession != null)
                {
                    requestedSession.setLastAccessedTime(Instant.now());
                }
                return isRequestedSessionIdValid(requestedSession);
            }
            return this.requestedSessionIdValid;
        }

        private boolean isRequestedSessionIdValid(S session)
        {
            if (this.requestedSessionIdValid == null)
            {
                this.requestedSessionIdValid = session != null;
            }
            return this.requestedSessionIdValid;
        }

        private boolean isInvalidateClientSession()
        {
            return getCurrentSession() == null && this.requestedSessionInvalidated;
        }

        @Override
        public HttpSession getSession(boolean create)
        {
            SessionRepositoryRequestWrapper.HttpSessionWrapper currentSession = getCurrentSession();
            if (currentSession != null)
            {
                return currentSession;
            }
            S requestedSession = getRequestedSession();
            if (requestedSession != null)
            {
                if (getAttribute(INVALID_SESSION_ID_ATTR) == null)
                {
                    requestedSession.setLastAccessedTime(Instant.now());
                    this.requestedSessionIdValid = true;
                    currentSession = new CustomedSessionRepositoryFilter.SessionRepositoryRequestWrapper.HttpSessionWrapper(requestedSession, getServletContext());
                    currentSession.setNew(false);
                    setCurrentSession(currentSession);
                    return currentSession;
                }
            } else
            {
                // This is an invalid session id. No need to ask again if
                // request.getSession is invoked for the duration of this request
                if (SESSION_LOGGER.isDebugEnabled())
                {
                    SESSION_LOGGER.debug(
                            "No session found by id: Caching datas for getSession(false) for this HttpServletRequest.");
                }
                setAttribute(INVALID_SESSION_ID_ATTR, "true");
            }
            if (!create)
            {
                return null;
            }

            //规避无效 session
            if (StringUtils.isBlank(SessionHolder.getUserIdentifier()))
            {
                return null;
            }

            if (SESSION_LOGGER.isDebugEnabled())
            {
                SESSION_LOGGER.debug(
                        "A new session was created. To help you troubleshoot where the session was created we provided a StackTrace (this is not an error). You can prevent this from appearing by disabling DEBUG logging for "
                                + SESSION_LOGGER_NAME,
                        new RuntimeException(
                                "For debugging purposes only (not an error)"));
            }
            S session = CustomedSessionRepositoryFilter.this.sessionRepository.createSession();
            session.setLastAccessedTime(Instant.now());
            currentSession = new SessionRepositoryRequestWrapper.HttpSessionWrapper(session, getServletContext());
            setCurrentSession(currentSession);
            return currentSession;
        }

        @Override
        public ServletContext getServletContext()
        {
            if (this.servletContext != null)
            {
                return this.servletContext;
            }
            // Servlet 3.0+
            return super.getServletContext();
        }

        @Override
        public HttpSession getSession()
        {
            return getSession(true);
        }

        @Override
        public String getRequestedSessionId()
        {
            if (this.requestedSessionId == null)
            {
                getRequestedSession();
            }
            return this.requestedSessionId;
        }

        @Override
        public RequestDispatcher getRequestDispatcher(String path)
        {
            RequestDispatcher requestDispatcher = super.getRequestDispatcher(path);
            return new CustomedSessionRepositoryFilter.SessionRepositoryRequestWrapper.SessionCommittingRequestDispatcher(requestDispatcher);
        }

        private S getRequestedSession()
        {
            if (!this.requestedSessionCached)
            {
                List<String> sessionIds =
                        CustomedSessionRepositoryFilter.this.httpSessionIdResolver
                                .resolveSessionIds(this);
                for (String sessionId : sessionIds)
                {
                    if (this.requestedSessionId == null)
                    {
                        this.requestedSessionId = sessionId;
                    }
                    S session = CustomedSessionRepositoryFilter.this.sessionRepository
                            .findById(sessionId);
                    if (session != null)
                    {
                        this.requestedSession = session;
                        this.requestedSessionId = sessionId;
                        break;
                    }
                }
                this.requestedSessionCached = true;
            }
            return this.requestedSession;
        }

        private void clearRequestedSessionCache()
        {
            this.requestedSessionCached = false;
            this.requestedSession = null;
            this.requestedSessionId = null;
        }

        /**
         * Allows creating an HttpSession from a Session instance.
         *
         * @author Rob Winch
         * @since 1.0
         */
        private final class HttpSessionWrapper
                extends HttpSessionAdapter<S>
        {

            HttpSessionWrapper(S session, ServletContext servletContext)
            {
                super(session, servletContext);
            }

            @Override
            public void invalidate()
            {
                super.invalidate();
                CustomedSessionRepositoryFilter.SessionRepositoryRequestWrapper.this.requestedSessionInvalidated = true;
                setCurrentSession(null);
                clearRequestedSessionCache();
                CustomedSessionRepositoryFilter.this.sessionRepository.deleteById(getId());
            }
        }

        /**
         * Ensures session is committed before issuing an include.
         *
         * @since 1.3.4
         */
        private final class SessionCommittingRequestDispatcher
                implements RequestDispatcher
        {

            private final RequestDispatcher delegate;

            SessionCommittingRequestDispatcher(RequestDispatcher delegate)
            {
                this.delegate = delegate;
            }

            @Override
            public void forward(ServletRequest request, ServletResponse response)
                    throws ServletException, IOException
            {
                this.delegate.forward(request, response);
            }

            @Override
            public void include(ServletRequest request, ServletResponse response)
                    throws ServletException, IOException
            {
                CustomedSessionRepositoryFilter.SessionRepositoryRequestWrapper.this.commitSession();
                this.delegate.include(request, response);
            }

        }

    }


    static class HttpSessionAdapter<S extends Session> implements HttpSession
    {

        private S session;

        private final ServletContext servletContext;

        private boolean invalidated;

        private boolean old;

        HttpSessionAdapter(S session, ServletContext servletContext)
        {
            if (session == null)
            {
                throw new IllegalArgumentException("session cannot be null");
            }
            if (servletContext == null)
            {
                throw new IllegalArgumentException("servletContext cannot be null");
            }
            this.session = session;
            this.servletContext = servletContext;
        }

        public void setSession(S session)
        {
            this.session = session;
        }

        public S getSession()
        {
            return this.session;
        }

        @Override
        public long getCreationTime()
        {
            checkState();
            return this.session.getCreationTime().toEpochMilli();
        }

        @Override
        public String getId()
        {
            return this.session.getId();
        }

        @Override
        public long getLastAccessedTime()
        {
            checkState();
            return this.session.getLastAccessedTime().toEpochMilli();
        }

        @Override
        public ServletContext getServletContext()
        {
            return this.servletContext;
        }

        @Override
        public void setMaxInactiveInterval(int interval)
        {
            this.session.setMaxInactiveInterval(Duration.ofSeconds(interval));
        }

        @Override
        public int getMaxInactiveInterval()
        {
            return (int) this.session.getMaxInactiveInterval().getSeconds();
        }

        @Override
        public HttpSessionContext getSessionContext()
        {
            return NOOP_SESSION_CONTEXT;
        }

        @Override
        public Object getAttribute(String name)
        {
            checkState();
            return this.session.getAttribute(name);
        }

        @Override
        public Object getValue(String name)
        {
            return getAttribute(name);
        }

        @Override
        public Enumeration<String> getAttributeNames()
        {
            checkState();
            return Collections.enumeration(this.session.getAttributeNames());
        }

        @Override
        public String[] getValueNames()
        {
            checkState();
            Set<String> attrs = this.session.getAttributeNames();
            return attrs.toArray(new String[0]);
        }

        @Override
        public void setAttribute(String name, Object value)
        {
            checkState();
            this.session.setAttribute(name, value);
        }

        @Override
        public void putValue(String name, Object value)
        {
            setAttribute(name, value);
        }

        @Override
        public void removeAttribute(String name)
        {
            checkState();
            this.session.removeAttribute(name);
        }

        @Override
        public void removeValue(String name)
        {
            removeAttribute(name);
        }

        @Override
        public void invalidate()
        {
            checkState();
            this.invalidated = true;
        }

        public void setNew(boolean isNew)
        {
            this.old = !isNew;
        }

        @Override
        public boolean isNew()
        {
            checkState();
            return !this.old;
        }

        private void checkState()
        {
            if (this.invalidated)
            {
                throw new IllegalStateException(
                        "The HttpSession has already be invalidated.");
            }
        }

        private static final HttpSessionContext NOOP_SESSION_CONTEXT = new HttpSessionContext()
        {

            @Override
            public HttpSession getSession(String sessionId)
            {
                return null;
            }

            @Override
            public Enumeration<String> getIds()
            {
                return EMPTY_ENUMERATION;
            }

        };

        private static final Enumeration<String> EMPTY_ENUMERATION = new Enumeration<String>()
        {

            @Override
            public boolean hasMoreElements()
            {
                return false;
            }

            @Override
            public String nextElement()
            {
                throw new NoSuchElementException("a");
            }

        };

    }

}
