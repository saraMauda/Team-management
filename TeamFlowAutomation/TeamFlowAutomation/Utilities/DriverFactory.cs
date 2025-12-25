using OpenQA.Selenium;
using OpenQA.Selenium.Chrome;

namespace TeamFlow_Automation.Infrastructure
{
    public static class DriverFactory
    {
        public static IWebDriver CreateChromeDriver()
        {
            ChromeOptions options = new ChromeOptions();
            options.AddArgument("--start-maximized");

            IWebDriver driver = new ChromeDriver(options);
            return driver;
        }
    }
}
