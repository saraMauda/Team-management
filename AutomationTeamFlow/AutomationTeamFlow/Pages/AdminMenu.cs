using OpenQA.Selenium;
using OpenQA.Selenium.Support.UI;
using SeleniumExtras.WaitHelpers;
using System;

namespace TeamFlow_Automation.Pages
{
    public class AdminMenu
    {
        private readonly IWebDriver Driver;
        private readonly WebDriverWait Wait;

        private readonly By overviewLink =
            By.CssSelector("a[routerlink='/admin-dashboard/overview']");

        private readonly By usersLink =
            By.CssSelector("a[routerlink='/admin-dashboard/users']");

        private readonly By projectsLink =
            By.CssSelector("a[routerlink='/admin-dashboard/projects']");

        private readonly By reportsLink =
            By.CssSelector("a[routerlink='/admin-dashboard/reports']");

        private readonly By meetingsLink =
            By.CssSelector("a[routerlink='/admin-dashboard/meetings']");

        public AdminMenu(IWebDriver driver)
        {
            Driver = driver;
            Wait = new WebDriverWait(driver, TimeSpan.FromSeconds(10));
        }

        private void Click(By by)
        {
            Wait.Until(ExpectedConditions.ElementToBeClickable(by)).Click();
        }

        public void GoToOverview()
        {
            Click(overviewLink);
        }

        public void GoToUsers()
        {
            Click(usersLink);
        }

        public void GoToProjects()
        {
            Click(projectsLink);
        }

        public void GoToReports()
        {
            Click(reportsLink);
        }

        public void GoToMeetings()
        {
            Click(meetingsLink);
        }
    }
}
