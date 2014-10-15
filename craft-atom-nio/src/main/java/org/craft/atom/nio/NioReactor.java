package org.craft.atom.nio;

import lombok.ToString;

import org.craft.atom.io.IoHandler;
import org.craft.atom.io.IoProcessorX;
import org.craft.atom.io.IoReactor;
import org.craft.atom.io.IoReactorX;
import org.craft.atom.nio.spi.NioBufferSizePredictorFactory;
import org.craft.atom.nio.spi.NioChannelEventDispatcher;

/**
 * Nio component provide a reactor pattern implementation. {@link NioReactor }
 * Represents a base reactor object.
 * 
 * @author mindwind
 * @version 1.0, Feb 21, 2013
 * @see NioAcceptor
 * @see NioProcessor
 * @see NioConnector
 */
@ToString(of = { "handler", "dispatcher", "predictorFactory" })
abstract public class NioReactor implements IoReactor {

	
	protected IoHandler                     handler         ;
	protected NioChannelEventDispatcher     dispatcher      ;
	protected NioBufferSizePredictorFactory predictorFactory;
	protected NioProcessorPool              pool            ;
	
	
	// ~ ----------------------------------------------------------------------------------------------------------

	
	@Override
	public void shutdown() {
		dispatcher.shutdown();
		pool.shutdown();
	}
	
	@Override
	public IoHandler getHandler() {
		return handler;
	}
	
	public NioChannelEventDispatcher getDispatcher() {
		return dispatcher;
	}
	
	public NioBufferSizePredictorFactory getPredictorFactory() {
		return predictorFactory;
	}
	
	protected IoReactorX x() {
		NioReactorX x = new NioReactorX();
		NioProcessor[] nps = pool.getPool();
		int nc = 0;
		int fc = 0;
		int cc = 0;
		for (NioProcessor np : nps) {
			IoProcessorX px = np.x();
			nc += px.newChannelCount();
			fc += px.flushingChannelCount();
			cc += px.closingChannelCount();
		}
		x.setNewChannelCount(nc);
		x.setFlushingChannelCount(fc);
		x.setClosingChannelCount(cc);
		x.setAliveChannelCount(pool.getIdleTimer().aliveChannels().size());
		return x;
	}

}
