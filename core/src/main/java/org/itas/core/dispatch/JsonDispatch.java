package org.itas.core.dispatch;

import org.itas.core.net.Message;

import com.uxuan.core.Dispatch;
import com.uxuan.core.User;

public class JsonDispatch extends Dispatch {

	@Override
	public void bind(String pack) throws Exception {
		
	}

	@Override
	public void unBind() {
		
	}

	@Override
	protected Handle getHandle(short clazzHead) {
		return null;
	}

	@Override
	protected void dispatchEvent(User user, Handle event, Message message)
			throws Exception {
		
	}

}
