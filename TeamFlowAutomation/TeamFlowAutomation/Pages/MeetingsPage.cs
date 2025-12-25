using OpenQA.Selenium;
using OpenQA.Selenium.Support.UI;
using SeleniumExtras.WaitHelpers;
using System;

namespace TeamFlow_Automation.Pages
{
    public class MeetingsPage
    {
        private readonly IWebDriver Driver;
        private readonly WebDriverWait Wait;

        private readonly By meetingsTitle =
                 By.XPath("//h2[contains(text(),'All Meetings')]");
        public MeetingsPage(IWebDriver driver)
        {
            Driver = driver;
            Wait = new WebDriverWait(driver, TimeSpan.FromSeconds(10));
        }

        public bool IsMeetingsTitleDisplayed()
        {
            return Wait
                .Until(ExpectedConditions.ElementIsVisible(meetingsTitle))
                .Displayed;
        }
    }
}
