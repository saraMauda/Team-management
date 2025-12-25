using OpenQA.Selenium;
using OpenQA.Selenium.Support.UI;
using SeleniumExtras.WaitHelpers;
using System;

namespace TeamFlow_Automation.Pages
{
    public abstract class BasePage
    {
        protected IWebDriver Driver;
        protected WebDriverWait Wait;

        protected BasePage(IWebDriver driver, int waitSeconds = 10)
        {
            Driver = driver;
            Wait = new WebDriverWait(driver, TimeSpan.FromSeconds(waitSeconds));
        }

        protected IWebElement WaitAndFind(By by) =>
            Wait.Until(ExpectedConditions.ElementIsVisible(by));

        protected void Click(By by) => WaitAndFind(by).Click();

        protected void Type(By by, string text)
        {
            var el = WaitAndFind(by);
            el.Clear();
            el.SendKeys(text);
        }

        public string GetTitle() => Driver.Title;
    }
}
