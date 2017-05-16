package com.artemzin.rxui.sample.java;

import com.artemzin.rxui.sample.java.AuthService.Failure;
import com.artemzin.rxui.sample.java.AuthService.Success;

import org.junit.Before;
import org.junit.Test;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

public class MainPresenterTest {

    TestAuthService authService = spy(new TestAuthService());
    Scheduler ioScheduler = Schedulers.trampoline();
    TestMainView view = new TestMainView();

    MainPresenter mainPresenter = new MainPresenter(authService, ioScheduler);

    @Before
    public void beforeEachTest() {
        mainPresenter.bind(view);
    }

    @Test
    public void shouldEnableSignInIfBothLoginAndPasswordAreNotEmpty() throws Exception {
        // WHEN login is not empty
        view.login.onNext("a");

        // AND password is not empty
        view.password.onNext("1");

        // THEN sign in should be enabled
        verify(view.signInEnable).accept(anyObject());
        verifyZeroInteractions(view.signInDisable);
    }

    @Test
    public void shouldNotEnableSignInIfLoginIsNotEmptyButPasswordIsEmpty() throws Exception {
        // WHEN login is not empty
        view.login.onNext("a");

        // AND password is empty
        view.password.onNext("");

        // THEN sign in should be disabled
        verify(view.signInDisable).accept(anyObject());
        verifyZeroInteractions(view.signInEnable);
    }

    @Test
    public void shouldNotEnableSignInIfLoginIsEmptyButPasswordIsNotEmpty() throws Exception {
        // WHEN login is empty
        view.login.onNext("");

        // AND password is not empty
        view.password.onNext("a");

        // THEN sign in should be disabled
        verify(view.signInDisable).accept(anyObject());
        verifyZeroInteractions(view.signInEnable);
    }

    @Test
    public void shouldSendRequestToAuthService() {
        // WHEN login is not empty (typing simulation)
        view.login.onNext("@art");
        view.login.onNext("@artem_zin");

        // AND password is not empty (typing simulation)
        view.password.onNext("123");
        view.password.onNext("123456");

        // AND click on sign in happens
        view.signInClicks.onNext(new Object());

        // THEN should call signIn service with correct credentials (not intermediate ones)
        verify(authService).signIn("@artem_zin", "123456");
        verifyNoMoreInteractions(authService);
    }

    @Test
    public void shouldSendSuccessSignInResultToView() throws Exception {
        // WHEN login is not empty
        view.login.onNext("abc");

        // AND password is not empty
        view.password.onNext("213");

        // AND click on sign in happens
        view.signInClicks.onNext(new Object());

        // AND signIn response arrives
        Success success = new Success();
        authService.signIn.onNext(success);

        // THEN should send signIn result to view
        verify(view.signInSuccess).accept(success);
        verifyZeroInteractions(view.signInFailure);
    }

    @Test
    public void shouldSendFailureSignInResultToView() throws Exception {
        // WHEN login is not empty
        view.login.onNext("abc");

        // AND password is not empty
        view.password.onNext("213");

        // AND click on sign in happens
        view.signInClicks.onNext(new Object());

        // AND signIn response arrives
        Failure failure = new Failure();
        authService.signIn.onNext(failure);

        // THEN should send signIn result to view
        verify(view.signInFailure).accept(failure);
        verifyZeroInteractions(view.signInSuccess);
    }
}
