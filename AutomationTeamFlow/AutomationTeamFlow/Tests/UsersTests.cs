using System;
using System.Data;
using System.IO;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using TeamFlow_Automation.Pages;
using AutomationTeamFlow.Utilities;

namespace TeamFlow_Automation.Tests
{
    [TestClass]
    public class UsersTests : BaseTest
    {
        [TestMethod]
        public void Admin_Can_Add_Users_From_Excel()
        {
            // Arrange
            var excelReader = new ExcelReader();
            string excelPath = @"D:\Users\User\Documents\TeamManagement\users.xlsx";

            var login = new LoginPage(Driver);
            login.GoTo();
            login.Login("m.adams@teamflow.com", "123");

            var menu = new AdminMenu(Driver);
            menu.GoToUsers();

            var usersPage = new UsersPage(Driver);

            DataTable usersTable = excelReader.ReadDataFromExcelFile(excelPath);

            foreach (DataRow row in usersTable.Rows)
            {
                string name = row["Name"].ToString();
                string email = row["Email"].ToString();
                string password = row["Password"].ToString();
                string role = row["Role"].ToString();

                usersPage.OpenAddUserForm();
                usersPage.AddUser(name, email, password, role);

                Assert.IsTrue(
                    usersPage.IsUserExists(email),
                    $"Error: User {email} not found in table."
                );
            }
        }
    }
}
