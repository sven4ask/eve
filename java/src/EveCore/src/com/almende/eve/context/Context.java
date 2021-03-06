package com.almende.eve.context;

import java.util.Map;

import com.almende.eve.scheduler.Scheduler;
import com.almende.eve.agent.AgentFactory;
import com.almende.eve.config.Config;
import com.almende.eve.json.JSONRequest;
import com.almende.eve.json.JSONResponse;


/**
 * @class Context
 * 
 * An interface for a context for Eve agents.
 * The context provides general information for the agent (about itself,
 * the environment, and the system configuration), and the agent can store its 
 * state in the context. 
 * The context extends a standard Java Map.
 * 
 * Usage:<br>
 *     ContextFactory factory = new FileContextFactory();<br>
 *     factory.setConfig(config);<br>
 *     Context context = factory.getContext("agentClass", "agentId");<br>
 *     context.put("key", "value");<br>
 *     System.out.println(context.get("key")); // "value"<br>
 * 
 * @author jos
 */
public interface Context extends Map<String, Object> {
	// servlet info
	public String getEnvironment();
	public String getServletUrl();	
	public Config getConfig();  // TODO: config should be read only
	
	// info about the agent
	public String getAgentId();
	public String getAgentClass();
	public String getAgentUrl();

	// scheduler
	public Scheduler getScheduler();
	
	// access to the AgentFactory, invoke or instantiate other agents 
	// (internal or external) via the context
	public JSONResponse invoke(String url, JSONRequest request) throws Exception;
	public AgentFactory getAgentFactory();

	// init and destroy methods
	public void init();     // executed once before the agent invocation
	public void destroy();  // executed once after the agent invocation
}
