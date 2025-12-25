using Microsoft.VisualStudio.TestTools.UnitTesting;
using TeamFlow_Automation.Pages;

namespace TeamFlow_Automation.Tests
{
    [TestClass]
    public class MeetingsTests : BaseTest
    {
        [TestMethod]
        public void Admin_Should_See_Meetings_Page()
        {
            var login = new LoginPage(Driver);
            login.Login("m.adams@teamflow.com", "123");

            var menu = new AdminMenu(Driver);
            menu.GoToMeetings();

            var meetings = new MeetingsPage(Driver);

            Assert.IsTrue(
                meetings.IsMeetingsTitleDisplayed(),
                "Meetings page title should be displayed"
            );
        }
    }
}
