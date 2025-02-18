using OfficeOpenXml;
using OxyPlot.Axes;
using OxyPlot.Series;
using OxyPlot;
using System.Data;
using System.Windows.Forms;
using System.Threading;
using System.Text;

namespace IA_pr3_v2
{
    public partial class Form1 : Form
    {
        DataTable dt;
        DataTable dt_real;
        double[,] dataSet_train;
        double[,] dataSet_test;
        double[,] dataSet_real_test;
        int train_Rows, test_Rows;

        public List<double> epoch_errors { get; private set; } = new List<double>();
        private List<double> test_Results = new List<double>();

        Neural_Network network;   //reteaua in sine (obiect)
        int hidden_Layer_Neurons = 0;
        int max_Epochs = 1000;             // nu sunt folosite
        double max_Error = 0.01;
        double learning_Rate = 0.05;

        public Form1()
        {
            InitializeComponent();

            dt = new DataTable();

            dataSet_train = new double[0, 0];

            radioButton_sigmoid.Checked = true;

            dataGridView_data.EnableHeadersVisualStyles = false;
        }


        // functie incarcare fisier de tip excel in aplicatie (train + test)
        private void Load_Excel_File(string filePath)
        {
            ExcelPackage.LicenseContext = LicenseContext.NonCommercial;

            using ExcelPackage package = new ExcelPackage(new FileInfo(filePath));
            ExcelWorksheet worksheet = package.Workbook.Worksheets[0];
            int total_Rows = worksheet.Dimension.End.Row - 1;
            int cols = worksheet.Dimension.End.Column;

            train_Rows = (int)(total_Rows * 0.7);    //procentul de train-test
            test_Rows = total_Rows - train_Rows;

            dataSet_train = new double[train_Rows, cols];
            dataSet_test = new double[test_Rows, cols];

            dt.Columns.Clear();
            for (int i = 1; i <= cols; i++)
            {
                dt.Columns.Add(worksheet.Cells[1, i].Value?.ToString() ?? $"Column {i}");
            }

            int rowIndex = 0;
            for (int row = 2; row <= worksheet.Dimension.End.Row; row++, rowIndex++)
            {
                DataRow newRow = dt.NewRow();
                for (int col = 1; col <= cols; col++)
                {
                    double value = Convert.ToDouble(worksheet.Cells[row, col].Value ?? "0");
                    newRow[col - 1] = value;

                    if (rowIndex < train_Rows)
                        dataSet_train[rowIndex, col - 1] = value;     // impartirea train-test
                    else
                        dataSet_test[rowIndex - train_Rows, col - 1] = value;
                }
                dt.Rows.Add(newRow);
            }

            dataGridView_data.DataSource = dt;
            Randomize_Rows();
            Color_Number_Rows();
        }

        //functie colorare train-test, numerotare randuri test
        private void Color_Number_Rows()
        {
            if (!dataGridView_data.Columns.Contains("RowNumber"))
            {
                dataGridView_data.Columns.Insert(0, new DataGridViewTextBoxColumn
                {
                    Name = "RowNumber",
                    HeaderText = "Row #",
                    ReadOnly = true
                });
            }

            int testRowIndex = 1;
            int labelColumnIndex = dataGridView_data.ColumnCount - 1;

            for (int i = 0; i < dataGridView_data.Rows.Count; i++)
            {
                if (i < train_Rows)
                {
                    dataGridView_data.Rows[i].DefaultCellStyle.BackColor = Color.Yellow;     //galben == train 
                    dataGridView_data.Rows[i].Cells["RowNumber"].Value = null;
                }
                else
                {
                    dataGridView_data.Rows[i].DefaultCellStyle.BackColor = Color.LightGray;    //gri == test; + numerotare

                    dataGridView_data.Rows[i].Cells["RowNumber"].Value = testRowIndex;
                    testRowIndex++;
                }
            }
            dataGridView_data.Refresh();
        }


        //functie de randomizare date antrenare (train)
        private void Randomize_Rows()
        {
            Random rnd = new Random();
            int rows = dataSet_train.GetLength(0);
            int cols = dataSet_train.GetLength(1);

            for (int i = rows - 1; i > 0; i--)
            {
                int j = rnd.Next(i + 1);

                for (int col = 0; col < cols; col++)
                {
                    double temp = dataSet_train[i, col];
                    dataSet_train[i, col] = dataSet_train[j, col];
                    dataSet_train[j, col] = temp;
                }

                for (int col = 0; col < dt.Columns.Count; col++)
                {
                    var tempValue = dt.Rows[i][col];
                    dt.Rows[i][col] = dt.Rows[j][col];
                    dt.Rows[j][col] = tempValue;
                }
            }

            dataGridView_data.Refresh();
        }


        //butonul de incarcare fisier excel (train + test)
        private void button_load_file_Click(object sender, EventArgs e)
        {
            OpenFileDialog openFileDialog = new OpenFileDialog();
            openFileDialog.Filter = "Excel Files|*.xlsx;*.xls";

            if (openFileDialog.ShowDialog() == DialogResult.OK)
            {
                string filePath = openFileDialog.FileName;
                Load_Excel_File(filePath);
            }
        }


        //functie de normalizare date de ANTRENARE, in functie de radio_button
        private void Data_Normalization_Train()
        {
            int rows = dataSet_train.GetLength(0);
            int cols = dataSet_train.GetLength(1);

            Dictionary<int, (double min, double max)> minMaxValues = new Dictionary<int, (double min, double max)>();

            for (int col = 0; col < cols; col++)
            {
                double min = double.MaxValue;
                double max = double.MinValue;

                for (int row = 0; row < rows; row++)                //cautare pe coloane, apoi pe randuri de maxime & minime
                {
                    double value = dataSet_train[row, col];
                    if (value < min) min = value;
                    if (value > max) max = value;
                }

                minMaxValues[col] = (min, max);
            }


            for (int row = 0; row < rows; row++)
            {
                for (int col = 0; col < cols; col++)
                {
                    double value = dataSet_train[row, col];
                    double min = minMaxValues[col].min;
                    double max = minMaxValues[col].max;

                    if (max != min)
                    {
                        double normalized_Value;

                        if (radioButton_sigmoid.Checked == true)
                        {

                            normalized_Value = (value - min) / (max - min);  //0 1  => sigmoid
                        }
                        else
                        {

                            normalized_Value = 2 * ((value - min) / (max - min)) - 1; // -1 1  => tanH; aducem din intervalul [0,1] in [-1,1]
                        }


                        dataSet_train[row, col] = normalized_Value;
                        dt.Rows[row][col] = normalized_Value.ToString("F6");
                    }
                }
            }

            if (dataGridView_data.InvokeRequired)
            {
                dataGridView_data.Invoke(new Action(() => dataGridView_data.Refresh()));
            }
            else
            {
                dataGridView_data.Refresh();
            }
        }


        //buton normalizare
        private void button_normalize_Click_1(object sender, EventArgs e)
        {
            Thread th = new Thread(() =>
            {
                if (dt.Rows.Count != 0)
                {
                    Data_Normalization_Train();
                }

            })
            { IsBackground = true };
            th.Start();
        }


        //functia de initilizare retea neuronala; parametrii sunt preluati din text_box - uri
        private void Initialize_Network()
        {

            if (!int.TryParse(textBox_hl_neurons.Text, out hidden_Layer_Neurons))
            {
                MessageBox.Show("Please give a number for the hidden neurons!");
                return;
            }

            if (!int.TryParse(textBox_max_epochs.Text, out max_Epochs))
            {
                MessageBox.Show("Please give a number for the maximum epochs!");
                return;
            }

            if (!double.TryParse(textBox_max_error.Text, out max_Error))
            {
                MessageBox.Show("Please give a number for the maximum error!");
                return;
            }

            if (!double.TryParse(textBox_learning_rate.Text, out learning_Rate))
            {
                MessageBox.Show("Please give a number for the learning rate!");
                return;
            }

            bool Sigmod_TanH = radioButton_sigmoid.Checked;


            //numar FIX de neuroni INPUT == 18 pt cicdis, 21 pt apa FARA TIME STAMP, 22 pt SDN, 8 pentru SYN FLOOD
            network = new Neural_Network(8, hidden_Layer_Neurons, Sigmod_TanH, learning_Rate)
            {
                max_epochs = max_Epochs,
                max_error = max_Error

            };
        }


        //butonul de antrenare a retelei. ATENTIE: trebuie sa avem parametrii, fisier excel, normalizare ...
        private void button_train_Click(object sender, EventArgs e)
        {
            Initialize_Network();

            // valori fixe (pt usurinta testarii)
            //network = new Neural_Network(18, 18, radioButton_sigmoid.Checked, 0.05)
            //{
            //    //max_epochs = max_Epochs,
            //    //max_error = max_Error

            //    max_epochs = 1000,
            //    max_error = 0.01

            //};

            Thread thread = new Thread(() =>
            {

                int rows = dataSet_train.GetLength(0);
                int cols = dataSet_train.GetLength(1) - 1;  // fara label

                double[][] inputs = new double[rows][];
                double[] expected_outputs = new double[rows];

                for (int i = 0; i < rows; i++)
                {
                    inputs[i] = new double[cols];
                    for (int j = 0; j < cols; j++)
                    {
                        inputs[i][j] = dataSet_train[i, j];
                    }
                    expected_outputs[i] = dataSet_train[i, cols];
                }

                network.Train(inputs, expected_outputs, radioButton_sigmoid.Checked);


                Plot_Training();
            })
            { IsBackground = true };
            thread.Start();

        }

        //functie de trasare (plot) a procesului de antrenare -> OxyPlot library (nuGet)
        private void Plot_Training()
        {
            var plotModel = new PlotModel { Title = "Training error per epoch" };

            var lineSeries = new LineSeries
            {
                Title = "Error",
                Color = OxyColors.Blue,
                MarkerType = MarkerType.Circle,
                MarkerSize = 3,
                MarkerStroke = OxyColors.Red
            };

            for (int i = 0; i < network.epoch_errors.Count; i++)
            {
                lineSeries.Points.Add(new DataPoint(i + 1, network.epoch_errors[i]));
            }

            plotModel.Series.Add(lineSeries);

            var xAxis = new LinearAxis
            {
                Position = AxisPosition.Bottom,
                Title = "Epoch",
                Minimum = 1,
                Maximum = network.epoch_errors.Count
            };
            plotModel.Axes.Add(xAxis);

            var yAxis = new LinearAxis
            {
                Position = AxisPosition.Left,
                Title = "Error",
                Minimum = 0,
                Maximum = network.epoch_errors.Max()
            };
            plotModel.Axes.Add(yAxis);


            plotView_training.Model = plotModel;
            plotView_training.InvalidatePlot(true);
        }


        //buton de testare, doar apelam functia de testare
        private void button_test_Click(object sender, EventArgs e)
        {
            Test_Data();
        }


        //functie de trasare (plot) a procesului de testare -> OxyPlot library (nuGet)
        private void Plot_Testing()
        {
            var plotModel = new PlotModel { Title = "Test results" };

            var lineSeries = new LineSeries
            {
                Title = "Test Predictions",
                Color = OxyColors.Blue,
                MarkerType = MarkerType.Circle,
                MarkerSize = 2,
                MarkerStroke = OxyColors.Black
            };

            for (int i = 0; i < test_Results.Count; i++)
            {
                lineSeries.Points.Add(new DataPoint(i + 1, test_Results[i]));
            }

            plotModel.Series.Add(lineSeries);
            plotModel.Axes.Add(new LinearAxis { Position = AxisPosition.Bottom, Title = "Rows" });
            plotModel.Axes.Add(new LinearAxis { Position = AxisPosition.Left, Title = "Prediction" });

            plotView_testing.Model = plotModel;
            plotView_testing.InvalidatePlot(true);
        }


        //functia de normalizare date de TESTARE, in functie de radio_button
        private void Data_Normalization_Test()
        {
            int rows = dataSet_test.GetLength(0);
            int cols = dataSet_test.GetLength(1);

            Dictionary<int, (double min, double max)> minMaxValues = new Dictionary<int, (double min, double max)>();

            for (int col = 0; col < cols; col++)
            {
                double min = double.MaxValue;
                double max = double.MinValue;

                for (int row = 0; row < dataSet_test.GetLength(0); row++)
                {
                    double value = dataSet_test[row, col]; 
                    if (value < min) min = value;
                    if (value > max) max = value;
                }

                minMaxValues[col] = (min, max);
            }

            for (int row = 0; row < rows; row++)
            {
                for (int col = 0; col < cols; col++)
                {
                    double value = dataSet_test[row, col];
                    double min = minMaxValues[col].min;
                    double max = minMaxValues[col].max;

                    if (max != min)
                    {
                        double normalizedValue;
                        if (radioButton_sigmoid.Checked)
                        {
                            normalizedValue = (value - min) / (max - min);
                        }
                        else
                        {
                            normalizedValue = 2 * ((value - min) / (max - min)) - 1;
                        }
                        dataSet_test[row, col] = normalizedValue;
                    }
                }
            }
        }


        //buton salvare tarii sinaptice (model) in fisier .txt 
        private void button_save_model_Click(object sender, EventArgs e)
        {
            SaveFileDialog saveFileDialog = new SaveFileDialog
            {
                Filter = "Text Files|*.txt",
                Title = "Save Model"
            };

            if (saveFileDialog.ShowDialog() == DialogResult.OK)
            {
                network.Save_Model(saveFileDialog.FileName);
                MessageBox.Show("Succes!");
            }
        }

        //buton incarcare model din fisier .txt ; ATENTIE: trebuie sa aiba exact numarul de neuroni de intrare
        private void button_load_model_Click(object sender, EventArgs e)
        {
            OpenFileDialog openFileDialog = new OpenFileDialog
            {
                Filter = "Text Files|*.txt",
                Title = "Load Model"
            };

            if (openFileDialog.ShowDialog() == DialogResult.OK)
            {
                try
                {
                    Initialize_Network();
                    network.Load_Model(openFileDialog.FileName);
                }
                catch (Exception ex)
                {
                    MessageBox.Show($"Error loading model: {ex.Message}");
                }
            }
        }


        // functie segmentare blocuri; necesara pt spike detection
        private List<double[]> Segment_Data(int block_Size)
        {
            List<double[]> dataBlocks = new List<double[]>();

            for (int i = 0; i < dataSet_test.GetLength(0); i += block_Size)
            {
                int current_Block_Size = Math.Min(block_Size, dataSet_test.GetLength(0) - i);
                double[] block = new double[current_Block_Size];

                for (int j = 0; j < current_Block_Size; j++)
                {
                    block[j] = dataSet_test[i + j, dataSet_test.GetLength(1) - 1]; //label 
                }

                dataBlocks.Add(block);
            }

            return dataBlocks;
        }


        //functie de detectie spike (concentrare mai mare de randuri ddos)
        private void Detect_Spikes(int block_Size, double spike_Threshold)
        {
            // log file 
            string logFileName = $"Scan Spikes- {DateTime.Now:yyyy-MM-dd_HH-mm-ss}.txt";
            string logFilePath = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments), logFileName);

            var dataBlocks = Segment_Data(block_Size);
            bool spikes_Detected = false;

            using (StreamWriter logFile = new StreamWriter(logFilePath, true))
            {
                for (int blockIndex = 0; blockIndex < dataBlocks.Count; blockIndex++)
                {
                    int ddos_Count = dataBlocks[blockIndex].Count(label => label == 1);
                    double ddos_Ratio = (double)ddos_Count / block_Size;

                    if (ddos_Ratio > spike_Threshold)
                    {
                        spikes_Detected = true;
                        logFile.WriteLine($"[{DateTime.Now}] Spike detected in Block {blockIndex + 1}: {ddos_Count}/{block_Size} rows are DDoS.");
                    }
                }
            }

            if (spikes_Detected)
            {
                MessageBox.Show("Spike detected! Check the log file for details.", "Spike Detection", MessageBoxButtons.OK, MessageBoxIcon.Warning);
            }
            else
            {
                MessageBox.Show("No spikes detected.", "Spike Detection", MessageBoxButtons.OK, MessageBoxIcon.Information);
            }
        }

        //functie de detectie spike consecutiv (atac ddos?)
        private void Detect_Consecutive_Spikes(int block_Size, double spike_Threshold, int consecutive_SpikeLimit)
        {
            //log file 
            string logFileName = $"Scan Consecutive Spikes - {DateTime.Now:yyyy-MM-dd_HH-mm-ss}.txt";
            string logFilePath = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments), logFileName);

            var dataBlocks = Segment_Data(block_Size);
            int consecutive_Spikes = 0;
            bool sustained_AttackDetected = false;

            using (StreamWriter logFile = new StreamWriter(logFilePath, true))
            {
                for (int blockIndex = 0; blockIndex < dataBlocks.Count; blockIndex++)
                {
                    int ddos_Count = dataBlocks[blockIndex].Count(label => label == 1);
                    double ddos_Ratio = (double)ddos_Count / block_Size;

                    if (ddos_Ratio > spike_Threshold)
                    {
                        consecutive_Spikes++;
                        logFile.WriteLine($"[{DateTime.Now}] Spike detected in Block {blockIndex + 1}. Consecutive Spikes: {consecutive_Spikes}");

                        if (consecutive_Spikes >= consecutive_SpikeLimit)
                        {
                            logFile.WriteLine($"[{DateTime.Now}] Sustained attack detected! Consecutive spike limit reached.");
                            sustained_AttackDetected = true;
                            break;
                        }
                    }
                    else
                    {
                        consecutive_Spikes = 0; // reset
                    }
                }
            }

            if (sustained_AttackDetected)
            {
                MessageBox.Show("Sustained attack detected! Check the log file for details.", "Consecutive Spike Detection", MessageBoxButtons.OK, MessageBoxIcon.Warning);
            }
            else
            {
                MessageBox.Show("No consecutive spikes detected.", "Consecutive Spike Detection", MessageBoxButtons.OK, MessageBoxIcon.Information);
            }
        }


        //functia de testare retea neuronala, dupa antrenare
       
        private void Test_Data()
        {
            try
            {
                Thread th = new Thread(() =>
                {
                    if (dataSet_test == null || dataSet_test.Length == 0)
                    {
                        MessageBox.Show("Please load a dataset and train the network before testing.");
                        return;
                    }

                    Data_Normalization_Test();

                    test_Results.Clear();
                    int false_Positives = 0;
                    int false_Negatives = 0;
                    int correct_Predictions = 0;  // nr predictii corecte

                    double threshold = radioButton_sigmoid.Checked ? 0.5 : 0.0;


                    int testRowOffset = dataGridView_data.Rows.Count - dataSet_test.GetLength(0);

                    for (int i = 0; i < dataSet_test.GetLength(0); i++)
                    {
                        double[] testInput = new double[dataSet_test.GetLength(1) - 1];
                        for (int j = 0; j < dataSet_test.GetLength(1) - 1; j++)
                        {
                            testInput[j] = dataSet_test[i, j];
                        }

                        double actual_Label = dataSet_test[i, dataSet_test.GetLength(1) - 1];
                        double prediction = network.Forward_Propagation(testInput, radioButton_sigmoid.Checked);
                        test_Results.Add(prediction);

                        int predicted_Label = prediction >= threshold ? 1 : 0;

                        if (predicted_Label == 1 && actual_Label == 0)
                        {
                            false_Positives++;
                            dataGridView_data.Rows[testRowOffset + i].DefaultCellStyle.BackColor = Color.OrangeRed;
                        }
                        else if (predicted_Label == 0 && actual_Label == 1)
                        {
                            false_Negatives++;
                            dataGridView_data.Rows[testRowOffset + i].DefaultCellStyle.BackColor = Color.DarkBlue;
                        }
                        else
                        {
                            correct_Predictions++; // incrementare predictii corecte
                        }
                    }


                    // accuracy
                    int total_Predictions = dataSet_test.GetLength(0);
                    double accuracy = (double)correct_Predictions / total_Predictions * 100;

                    // spike detection
                    int block_Size = 10;
                    double spike_Threshold = 0.5;
                    int consecutive_SpikeLimit = 5;

                    Detect_Spikes(block_Size, spike_Threshold);
                    Detect_Consecutive_Spikes(block_Size, spike_Threshold, consecutive_SpikeLimit);

                    dataGridView_data.Refresh();

                    // rezultate testare (acuratete, fals neg, fals poz
                    MessageBox.Show($"Testing complete:\nFalse Positives: {false_Positives}\nFalse Negatives: {false_Negatives}\nAccuracy: {accuracy:F2}%");
                    Plot_Testing();
                })
                { IsBackground = true };
                th.Start();

            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
        }


        //testarea pentru date reale, fara label
        private void button_real_test_Click(object sender, EventArgs e)
        {
            try
            {
                using (OpenFileDialog openFileDialog = new OpenFileDialog())
                {
                    openFileDialog.Filter = "Excel Files|*.xlsx";
                    if (openFileDialog.ShowDialog() == DialogResult.OK)
                    {
                        string filePath = openFileDialog.FileName;

                        // incarcare date reale
                        LoadExcelData_for_Real_Testing(filePath);

                        // normalizare date reale
                        Data_Normalization_for_Real_Testing();

                        // predict (forward prop., labels)
                        Testing_for_Real_Data(dataSet_real_test, dt_real);

                        // salvare rezultate in tabela excel
                        Save_real_DataTable_to_Excel(dt_real, filePath.Replace(".xlsx", "_classified.xlsx"));

                        MessageBox.Show("Real data testing complete. Results saved to new Excel file.");
                    }
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("An error occurred: " + ex.Message);
            }
        }


        //functia pentru incarcarea datelor reale
        private void LoadExcelData_for_Real_Testing(string filePath)
        {
            ExcelPackage.LicenseContext = LicenseContext.NonCommercial;

            using ExcelPackage package = new ExcelPackage(new FileInfo(filePath));
            ExcelWorksheet worksheet = package.Workbook.Worksheets[0];
            int totalRows = worksheet.Dimension.End.Row - 1;
            int cols = worksheet.Dimension.End.Column;

            dataSet_real_test = new double[totalRows, cols];
            dt_real = new DataTable();

            for (int i = 1; i <= cols; i++)
            {
                dt_real.Columns.Add(worksheet.Cells[1, i].Value?.ToString() ?? $"Column {i}");
            }

            int rowIndex = 0;
            for (int row = 2; row <= worksheet.Dimension.End.Row; row++, rowIndex++)
            {
                DataRow newRow = dt_real.NewRow();
                for (int col = 1; col <= cols; col++)
                {
                    double value = Convert.ToDouble(worksheet.Cells[row, col].Value ?? "0");
                    newRow[col - 1] = value;
                    dataSet_real_test[rowIndex, col - 1] = value;
                }
                dt_real.Rows.Add(newRow);
            }

            dataGridView_data.DataSource = dt_real;
        }


        //functia de normalizare a datelor reale 
        private void Data_Normalization_for_Real_Testing()
        {
            int rows = dataSet_real_test.GetLength(0);
            int cols = dataSet_real_test.GetLength(1);

            Dictionary<int, (double min, double max)> minMaxValues = new Dictionary<int, (double min, double max)>();

            for (int col = 0; col < cols; col++)
            {
                double min = double.MaxValue;
                double max = double.MinValue;

                for (int row = 0; row < rows; row++)
                {
                    double value = dataSet_real_test[row, col];
                    if (value < min) min = value;
                    if (value > max) max = value;
                }

                minMaxValues[col] = (min, max);
            }

            for (int row = 0; row < rows; row++)
            {
                for (int col = 0; col < cols; col++)
                {
                    double value = dataSet_real_test[row, col];
                    double min = minMaxValues[col].min;
                    double max = minMaxValues[col].max;

                    if (max != min)
                    {
                        double normalizedValue;
                        if (radioButton_sigmoid.Checked)
                        {
                            normalizedValue = (value - min) / (max - min);
                        }
                        else
                        {
                            normalizedValue = 2 * ((value - min) / (max - min)) - 1;
                        }
                        dataSet_real_test[row, col] = normalizedValue;
                    }
                }
            }
        }

        //functia de testare, forward propagation, a datelor reale
        private void Testing_for_Real_Data(double[,] dataSet, DataTable dataTable)
        {
            int rowCount = dataSet.GetLength(0);
            for (int row = 0; row < rowCount; row++)
            {
                double[] inputRow = new double[dataSet.GetLength(1)];
                for (int col = 0; col < dataSet.GetLength(1); col++)
                {
                    inputRow[col] = dataSet[row, col];
                }

                double prediction = network.Forward_Propagation(inputRow, radioButton_sigmoid.Checked);
                int predictedLabel = prediction >= 0.5 ? 1 : 0;

                dataTable.Rows[row]["Prediction"] = predictedLabel;     // 1 sau 0
            }
        }


        //functia de salvare a datelor obtinute in urma testarii datelor reale
        //afisam si in data_table, in aplicatie
        private void Save_real_DataTable_to_Excel(DataTable dt, string filePath)
        {
            using (var workbook = new ClosedXML.Excel.XLWorkbook())
            {
                var worksheet = workbook.Worksheets.Add("Results");
                worksheet.Cell(1, 1).InsertTable(dt);
                workbook.SaveAs(filePath);
            }
        }


    }
}
