using OfficeOpenXml;
using System.Data;
using System.IO;

namespace AutomationTeamFlow.Utilities
{
    internal class ExcelReader
    {
        public DataTable ReadDataFromExcelFile(string file)
        {
            ExcelPackage.LicenseContext = LicenseContext.NonCommercial;

            var filePath = new FileInfo(file);
            DataTable dataTable = new DataTable();

            using (ExcelPackage package = new ExcelPackage(filePath))
            {
                ExcelWorksheet worksheet = package.Workbook.Worksheets[0];

                for (int col = 1; col <= worksheet.Dimension.End.Column; col++)
                {
                    dataTable.Columns.Add(worksheet.Cells[1, col].Text);
                }

                for (int row = 2; row <= worksheet.Dimension.End.Row; row++)
                {
                    DataRow dataRow = dataTable.NewRow();
                    for (int col = 1; col <= worksheet.Dimension.End.Column; col++)
                    {
                        dataRow[col - 1] = worksheet.Cells[row, col].Text;
                    }
                    dataTable.Rows.Add(dataRow);
                }
            }

            return dataTable;
        }
    }
}
