package net.dublin.bus.ui.view.realtime;

public class RealTimePresenterTest {
//    private static final String STOP_NUMBER = "2";
//    private static final List<StopData> STOP_DATA = new ArrayList<>();
//    private static final List<StopData> STOP_DATA_EMPTY = new ArrayList<>();
//
//    @Mock
//    private RealTimeRepository repository;
//
//    @Mock
//    private RealTimeContract.View view;
//
//    private RealTimePresenter presenter;
//
//    @Before
//    public void setupRepositoryPresenter() {
//        MockitoAnnotations.initMocks(this);
//
//        //presenter = new RealTimePresenter(view, repository, STOP_NUMBER);
//        STOP_DATA.add(new StopData());
//        STOP_DATA.add(new StopData());
//        STOP_DATA.add(new StopData());
////
////        // Override RxJava schedulers
////        RxJavaHooks.setOnIOScheduler(new Func1<Scheduler, Scheduler>() {
////            @Override
////            public Scheduler call(Scheduler scheduler) {
////                return Schedulers.immediate();
////            }
////        });
//
////        RxJavaHooks.setOnComputationScheduler(new Func1<Scheduler, Scheduler>() {
////            @Override
////            public Scheduler call(Scheduler scheduler) {
////                return Schedulers.immediate();
////            }
////        });
//
////        RxJavaHooks.setOnNewThreadScheduler(new Func1<Scheduler, Scheduler>() {
////            @Override
////            public Scheduler call(Scheduler scheduler) {
////                return Schedulers.immediate();
////            }
////        });
//
//        // Override RxAndroid schedulers
//        //final RxAndroidPlugins rxAndroidPlugins = RxAndroidPlugins.getInstance();
////        rxAndroidPlugins.registerSchedulersHook(new RxAndroidSchedulersHook() {
////            @Override
////            public Scheduler getMainThreadScheduler() {
////                return Schedulers.immediate();
////            }
////        });
//
////
////        RxJavaPlugins.setInitIoSchedulerHandler(new Function<Callable<Scheduler>, Scheduler>() {
////            @Override
////            public Scheduler apply(Callable<Scheduler> schedulerCallable) throws Exception {
////                return Schedulers.trampoline();
////            }
////        });
////
////        RxJavaPlugins.setInitComputationSchedulerHandler(new Function<Callable<Scheduler>, Scheduler>() {
////            @Override
////            public Scheduler apply(Callable<Scheduler> schedulerCallable) throws Exception {
////                return Schedulers.trampoline();
////            }
////        });
////
////
////        RxJavaPlugins.setInitNewThreadSchedulerHandler(new Function<Callable<Scheduler>, Scheduler>() {
////            @Override
////            public Scheduler apply(Callable<Scheduler> schedulerCallable) throws Exception {
////                return Schedulers.trampoline();
////            }
////        });
////
////
////        RxJavaPlugins.setInitSingleSchedulerHandler(new Function<Callable<Scheduler>, Scheduler>() {
////            @Override
////            public Scheduler apply(Callable<Scheduler> schedulerCallable) throws Exception {
////                return Schedulers.trampoline();
////            }
////        });
////
////
////        RxAndroidPlugins.setInitMainThreadSchedulerHandler(new Function<Callable<Scheduler>, Scheduler>() {
////            @Override
////            public Scheduler apply(Callable<Scheduler> schedulerCallable) throws Exception {
////                return Schedulers.trampoline();
////            }
////        });
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        //RxJavaHooksooks.reset();
//        //RxAndroidPlugins.getInstance().reset();
//        STOP_DATA.clear();
//    }
//
//    @Test
//    public void loadData_1_NoConnection_ShowsErrorUi() {
//        when(repository.getData(STOP_NUMBER)).thenReturn(Observable.<List<StopData>>error(new Exception()));
//        when(view.hasNetwork()).thenReturn(false);
//
//        presenter.loadData();
//
//        verify(view).hideNoData();
//        verify(view).showProgress();
//
//        verify(view).hideProgress();
//        verify(view).hideProgressSwipe();
//        verify(view).showSnackBarNoConnection();
//    }
//
//    @Test
//    public void loadData_LoadIntoView() {
//        when(repository.getData(STOP_NUMBER)).thenReturn(Observable.just(STOP_DATA));
//
//        presenter.loadData();
//
//        verify(view).hideNoData();
//        verify(view).showProgress();
//        verify(view).showData(STOP_DATA);
//        verify(view).hideProgress();
//        verify(view).hideProgressSwipe();
//    }
//
//    @Test
//    public void loadData_Refresh_LoadIntoView() {
//        when(repository.getData(STOP_NUMBER)).thenReturn(Observable.just(STOP_DATA));
//        when(view.getSizeData()).thenReturn(3);
//
//        presenter.loadData();
//
//        verify(view).hideNoData();
//        verify(view).showProgressSwipe();
//        verify(view).showData(STOP_DATA);
//        verify(view).hideProgress();
//        verify(view).hideProgressSwipe();
//    }
//
//    @Test
//    public void loadData_NoItems_LoadIntoView() {
//        when(repository.getData(STOP_NUMBER)).thenReturn(Observable.just(STOP_DATA_EMPTY));
//
//        presenter.loadData();
//
//        verify(view).hideNoData();
//        verify(view).showProgress();
//        verify(view).showNoData();
//        verify(view).hideProgress();
//        verify(view).hideProgressSwipe();
//    }
//
//    @Test
//    public void loadData_ErrorConnection_ShowsErrorUi() {
//        when(repository.getData(STOP_NUMBER)).thenReturn(Observable.<List<StopData>>error(new Exception()));
//        when(view.hasNetwork()).thenReturn(true);
//
//        presenter.loadData();
//
//        verify(view).hideNoData();
//        verify(view).showProgress();
//
//        verify(view).hideProgress();
//        verify(view).hideProgressSwipe();
//        verify(view).showSnackBarError();
//    }
}
