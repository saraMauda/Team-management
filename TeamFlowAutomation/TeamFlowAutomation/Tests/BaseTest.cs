using Microsoft.VisualStudio.TestTools.UnitTesting;
using OpenQA.Selenium;
using TeamFlow_Automation.Infrastructure;

namespace TeamFlow_Automation.Tests
{
    [TestClass]
    public class BaseTest
    {
        protected IWebDriver Driver;

        [TestInitialize]
        public void Setup()
        {
            Driver = DriverFactory.CreateChromeDriver();
            Driver.Navigate().GoToUrl("http://localhost:4200/login");
        }

        [TestCleanup]
        public void Cleanup()
        {
            if (Driver != null)
            {
                Driver.Quit();
            }
        }
    }
}
