using OfficeOpenXml;
using System;
using System.Data;
using System.IO;

namespace AutomationTeamFlow.Utilities
{
    internal class ExcelReader
    {
        public ExcelReader()
        {
            ExcelPackage.LicenseContext = LicenseContext.NonCommercial;
        }

        public DataTable ReadDataFromExcelFile(string file)
        {
            ExcelPackage.LicenseContext = LicenseContext.NonCommercial;

            if (!File.Exists(file))
            {
                throw new FileNotFoundException($"Excel file not found at path: {file}");
            }

            FileInfo fileInfo = new FileInfo(file);
            DataTable dataTable = new DataTable();

            using (ExcelPackage package = new ExcelPackage(fileInfo))
            {
                // בגרסה 7 זה בדרך כלל אינדקס 0 או 1. אם 0 נכשל, נסי 1.
                ExcelWorksheet worksheet = package.Workbook.Worksheets[0];

                int totalRows = worksheet.Dimension.End.Row;
                int totalCols = worksheet.Dimension.End.Column;

                for (int col = 1; col <= totalCols; col++)
                {
                    dataTable.Columns.Add(worksheet.Cells[1, col].Text);
                }

                for (int row = 2; row <= totalRows; row++)
                {
                    DataRow dataRow = dataTable.NewRow();
                    for (int col = 1; col <= totalCols; col++)
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