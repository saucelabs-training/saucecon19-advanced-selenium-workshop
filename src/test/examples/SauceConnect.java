package test.examples;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class SauceConnect {
    private WebDriver driver;

    @Rule
    public TestRule watcher = new TestWatcher() {
        @Override
        protected void succeeded(Description description) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("sauce:job-result=passed");
            driver.quit();
        }

        @Override
        protected void failed(Throwable e, Description description) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("sauce:job-result=failed");
            driver.quit();
        }
    };

    @Rule
    public TestName name = new TestName();

    // Sauce Connect started on local machine with: 
    // sc --tunnel-identifier ORANGE --shared-tunnel --no-remove-colliding-tunnels --pidfile /tmp/pid0.log'

    @Before
    public void setup() throws MalformedURLException {
        MutableCapabilities sauceOptions = new MutableCapabilities();
        sauceOptions.setCapability("name", name.getMethodName());
        sauceOptions.setCapability("build", "With Demo User");
        sauceOptions.setCapability("tunnelIdentifier", "ORANGE");
        sauceOptions.setCapability("parentTunnel", "titusfortner");

        MutableCapabilities options = new MutableCapabilities();
        options.setCapability("browserName", "chrome");
        options.setCapability("browserVersion", "latest");
        options.setCapability("platformName", "Windows 10");
        options.setCapability("sauce:options", sauceOptions);

        String user = System.getenv("SAUCE_USER_DEMO");
        String key = System.getenv("SAUCE_KEY_DEMO");
        URL url = new URL("https://" + user + ":" + key + "@ondemand.saucelabs.com:443/wd/hub");

        driver = new RemoteWebDriver(url, options);
    }

    @Test
    public void demoUser() {
        driver.get("https://www.saucedemo.com/");

        String email = "standard_user";
        String password = "secret_sauce";

        driver.findElement(By.id("user-name")).clear();
        driver.findElement(By.id("user-name")).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.className("btn_action")).click();
    }
}
