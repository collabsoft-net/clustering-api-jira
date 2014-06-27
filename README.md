# Clustering API for Atlassian JIRA #


The Clustering API for Atlassian JIRA add-ons project is designed to create a backwards-compatible library for caching and scheduling in Atlassian JIRA add-ons. The goal is to create an API that can be used in both clustered (JIRA 6.3 and above) and non-clustered (JIRA 5.0 to 6.2.x) instances.
        
The Cache API will use in-memory EhCache as a fall-back if the CacheManager service is not available.
        
Scheduling will use the Atlassian Concurrent Utilities library (com.atlassian.util.concurrent.ThreadFactories) in combination with the JAVA ExecutorsService interface to schedule daemon services in case the clustered SchedulerService is not available.

## Usage ##

Include the clustering-api-jira library to your add-on by cloning it and running *mvn clean install*.
Add the Maven dependency in your project POM:

```
<dependency>
    <groupId>net.collabsoft</groupId>
    <artifactId>clustering-api-jira</artifactId>
    <version>0.9.2</version>
</dependency>
```

### Cache API ###

The Cache API has been build to work almost identical to the Atlassian implementation. The main goal is to add support for in-memory cache when the Atlassian Caching API is not available while using the same interface. The library is far more extensible than the example covered in this readme, but this should get you started.

To use the Cache API, include the following line into your *atlassian-plugin.xml*

```
<component key="cacheManager" name="CacheManager" class="net.collabsoft.clustering.jira.cache.DefaultCacheManager" />
```

Now add the CacheManager to your class constructor and create the appropriate caches:

```
#!java
public class ExampleManagerClass {

    private final String CACHE_KEY = ExampleManagerClass.class.getName() + ":cache";
    private final String CACHE_ENTRY_KEY = "SomeCacheObject";

    private Cache cache;

    public ExampleManagerClass(CacheManager cacheManager) {
        
        this.cache = cacheManager.getCache(CACHE_KEY, new AbstractCacheLoader() {
            @Override
            public Object getObjectForKey(String key) {
                // RETURN THE APPROPRIATE OBJECT
            }
        }, new CacheSettingsBuilder().maxEntries(10).expireAfterWrite(10, TimeUnit.Minutes).build());
        
    }
}
```

If you wish to use the *getCache(String name)* and *getCache(String name, CacheSettings settings)* methods to retrieve the cache, you must register a ClassLoader to the DefaultCacheManager in order to be able to retrieve cache object data:

```
#!java

cacheManager.registerCacheLoader(new AbstractCacheLoader() {
    @Override
    public Object getObjectForKey(String key) {
        // RETURN THE APPROPRIATE OBJECT
    }
}
this.cache = cacheManager.getCache(CACHE_KEY);
```

This will initialise the Cache with an instance of DefaultCacheSettings. Upon retrieval of the cache entry it will loop over all registered CacheLoader instances to retrieve the associated object.

### Scheduler API ###

**SchedulerFactory component**

To use the Scheduler API you will need to create a SchedulerFactory class.

```
#!java
public class ExampleSchedulerFactory extends AbstractSchedulerFactory {

    private static final Logger log = Logger.getLogger(ExampleSchedulerFactory.class);
    
    public static final String INSTANCE_KEY = ExampleSchedulerFactory.class.getName() + ":instance";
    
    // ----------------------------------------------------------------------------------------------- Constructor
    
    public ExampleSchedulerFactory(EventPublisher eventPublisher) {
        super(eventPublisher);
    }
    
    // ----------------------------------------------------------------------------------------------- Public Getters & Setters

    @Override
    public String getPluginKey() {
        return "org.example.scheduler";
    }
    
    @Override
    public Scheduler getLocalScheduler() {
        return new ExampleLocalScheduler();
    }

    @Override
    public Scheduler getClusteredScheduler() {
        return new ExampleClusteredScheduler((SchedulerService) getSchedulerService());
    }
    
    // ----------------------------------------------------------------------------------------------- Event Handlers
    
    @Override
    public void onStart() {
        getScheduler().unschedulePreviouslyScheduledJob();
        Scheduler scheduler = getScheduler();
        getScheduler().schedule(scheduler.getInterval());
    }
    
    // ----------------------------------------------------------------------------------------------- Public Methods
    
    public static void executeTask(Map<String, Object> map) {
        Object someParameter = (Object)map.get("someKey");
        // DO SOMETHING WITH THE PARAMETER, OR ANYTHING ELSE YOU WANT EXECUTED
    }
    
}

```

Add this to the *atlassian-plugin.xml* for injection:

```
<component key="schedulerFactory" name="SchedulerFactory" class="org.example.scheduler.ExampleSchedulerFactory" />
```

The goal of the SchedulerFactory is to make sure the Scheduler is started onPluginEnabled(), OnStart() and afterPropertiesSet() as well as stopped onPluginDisableEvent() and destroy(). This is also the class that will determine wether to use the LocalScheduler or ClusteredScheduler based on the JIRA version and the availability of the SchedulerService component.

**Scheduler Interface**

The implementation of the Scheduler interface is where all the action takes place, albeit almost invisible when using the AbstractLocalScheduler for pre-JIRA-6.3 and AbstractClusteredScheduler for 6.3+ instances. The LocalScheduler and the ClusteredScheduler in this example look almost the same, yet the have distinct differences.

*ExampleLocalScheduler*
```
#!java
public class ExampleLocalScheduler extends AbstractLocalScheduler {

    private static final Logger log = Logger.getLogger(ExampleLocalScheduler.class);
    public static final String INSTANCE_KEY = ExampleLocalScheduler.class.getName() + ":instance";

    // ----------------------------------------------------------------------------------------------- Constructor

    public ExampleLocalScheduler() {
    }
    
    // ----------------------------------------------------------------------------------------------- Getters & Setters

    @Override
    public Long getInterval() {
        //return the schedule interval (in seconds);
    }

    @Override
    public Map<String, Object> getJobData() {
        return new HashMap<String,Object>() {{
            put(ExampleLocalScheduler.INSTANCE_KEY, ExampleLocalScheduler.this);
            // You can add more parameters which can be retrieved upon task execution
        }};
    }
    
    @Override
    public ExampleLocalUpdateTask getPluginJob() {
        return new ExampleLocalUpdateTask();
    }
    
    @Override
    public String getPluginKey() {
        return "org.example.scheduler";
    }

    // ----------------------------------------------------------------------------------------------- Public methods


    // ----------------------------------------------------------------------------------------------- Private methods


    // ----------------------------------------------------------------------------------------------- Private Getters & Setters

    @Override
    public void schedule(Long interval) {
        schedule(getRunnableJob(getPluginJob(), getJobData()), getJobName(), interval);
        log.info(String.format("Our example background update service has been scheduled with an inverval of %s seconds.", getInterval()));
    }
}
```

*ExampleClusteredScheduler*
```
#!java
public class ExampleClusteredScheduler extends AbstractClusteredScheduler {

    private static final Logger log = Logger.getLogger(ExampleClusteredScheduler.class);
    
    public static final String INSTANCE_KEY = ExampleClusteredScheduler.class.getName() + ":instance";
    
    // ----------------------------------------------------------------------------------------------- Constructor
    
    public ExampleClusteredScheduler(SchedulerService schedulerService) {
        super(schedulerService);
    }
    
    // ----------------------------------------------------------------------------------------------- Public Getters & Setters

    @Override
    public Long getInterval() {
        //return the schedule interval (in seconds);
    }
    
    @Override
    public Map<String, Object> getJobData() {
        return new HashMap<String,Object>() {{
            put(ExampleClusteredScheduler.INSTANCE_KEY, ExampleClusteredScheduler.this);
            // You can add more parameters which can be retrieved upon task execution
        }};
    }
    
    @Override
    public AbstractClusteredTask getPluginJob() {
        return new ExampleClusteredUpdateTask();
    }
    
    @Override
    public String getPluginKey() {
        return "org.example.scheduler";
    }
        
    // ----------------------------------------------------------------------------------------------- Public Methods

    @Override
    public void schedule(Long interval) {
        try {
            schedule(getPluginJob(), interval);
            log.info(String.format("Our example background update service has been scheduled with an inverval of %s seconds.", getInterval()));
        } catch (SchedulerServiceException ex) {
            log.error(ex);
        }
    }
}
```

**Task Interface**

The Task interface, like the Scheduler, is almost identical between the LocalTask and ClusteredTask.
The main difference between the LocalTask and the ClusteredTask is the fact that the SchedulerService expects a JobRunnerResponse as a result of the task execution.

*ExampleLocalTask*
```
#!java
public class ExampleLocalTask extends AbstractLocalTask {

    // ----------------------------------------------------------------------------------------------- Constructor

    public ExampleLocalTask() {
        
    }

    // ----------------------------------------------------------------------------------------------- Public methods

    @Override
    public void execute(Map<String, Object> map) {
        ExampleSchedulerFactory.executeTask(map);
    }
}
```
*ExampleClusteredTask*
```
#!java
public class ExampleClusteredTask extends AbstractLocalTask {

    // ----------------------------------------------------------------------------------------------- Constructor

    public ExampleClusteredTask() {
        
    }

    // ----------------------------------------------------------------------------------------------- Public methods

    @Override
    public JobRunnerResponse execute(Map<String, Object> map) {
        ExampleSchedulerFactory.executeTask(map);
    }
}
```

## Contribution ##

Please do give feedback, create issues, fork, test, use, send pull requests. 
Any form of contribution is highly appreciated.