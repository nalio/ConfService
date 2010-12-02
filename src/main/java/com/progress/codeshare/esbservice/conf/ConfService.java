package com.progress.codeshare.esbservice.conf;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import com.sonicsw.xq.XQConstants;
import com.sonicsw.xq.XQEnvelope;
import com.sonicsw.xq.XQInitContext;
import com.sonicsw.xq.XQMessage;
import com.sonicsw.xq.XQParameters;
import com.sonicsw.xq.XQServiceContext;
import com.sonicsw.xq.XQServiceEx;
import com.sonicsw.xq.XQServiceException;

public final class ConfService implements XQServiceEx {
	private static final String PARAM_FILE = "file";

	public void destroy() {
	}

	public void init(XQInitContext ctx) {
	}

	public void service(final XQServiceContext ctx) throws XQServiceException {

		try {
			final Properties conf = new Properties();

			final XQParameters params = ctx.getParameters();

			final String file = params.getParameter(PARAM_FILE,
					XQConstants.PARAM_STRING);

			conf.load(new BufferedInputStream(new ByteArrayInputStream(file
					.getBytes())));

			final Collection keySet = conf.keySet();

			while (ctx.hasNextIncoming()) {
				final XQEnvelope env = ctx.getNextIncoming();

				final XQMessage msg = env.getMessage();

				final Iterator keyIterator = keySet.iterator();

				while (keyIterator.hasNext()) {
					final String key = (String) keyIterator.next();

					msg.setHeaderValue(key, conf.get(key));
				}

				final Iterator addressIterator = env.getAddresses();

				if (addressIterator.hasNext())
					ctx.addOutgoing(env);

			}

		} catch (final Exception e) {
			throw new XQServiceException(e);
		}

	}

	public void start() {
	}

	public void stop() {
	}

}