using System;
using System.Net;
using System.Data;
using System.Data.SqlClient;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using MySql.Data.MySqlClient;
using System.Globalization;






//server tcp/ip pt aplicatie android 
//functioneaza si in situatia noastra, afiseaza mesajul
//vom folosi hercule, ii dam ip si port 8000, si un mesaj
//se va afisa mesajul pe consola



class Server
{
    private static readonly string connectionString = "Server=localhost;Database=music;User Id = root; Password=;";

    static void Main()
    {
        TcpListener server = null;

        try
        {
      
            IPAddress ipAddress = IPAddress.Parse("192.168.0.151");   //trebuie adresa sistemului pe care se ruleaza
            int port = 8000;

            server = new TcpListener(ipAddress, port);
            server.Start();

            Console.WriteLine("Server started on " + ipAddress + ":" + port);

            while (true)
            {
                //acceptarea conexiunii
                TcpClient client = server.AcceptTcpClient();
                Console.WriteLine("Client connected");

               //  HandleClientCommunication(client); //un singur client

                //clienti multiplii
                Thread clientThread = new Thread(() => HandleClientCommunication(client));
                clientThread.Start();

               
            }
        }
        catch (Exception e)
        {
            Console.WriteLine("Error: " + e.Message);
        }
        finally
        {
            server?.Stop();
        }
    }

    static void HandleClientCommunication(TcpClient client)
    {

        try
        {
            NetworkStream stream = client.GetStream();

            string message = ReadMessage(stream);

            Console.WriteLine($"Received :" + message);

            string responseMessage = ProcessParameters(message);
            
            WriteMessage(stream, responseMessage);
        }
        catch (Exception e)
        {
            Console.WriteLine("Error handling client: " + e.Message);
        }
        finally
        {
            //closing the connection
            client.Close();
            Console.WriteLine("Server closed");
        }
    }


    static string ReadMessage(NetworkStream stream)
    {
        byte[] data = new byte[256];
        int bytesRead = stream.Read(data, 0, data.Length);
        return Encoding.ASCII.GetString(data, 0, bytesRead);
    }

    static void WriteMessage(NetworkStream stream, string message)
    {
        byte[] data = Encoding.ASCII.GetBytes(message);
        stream.Write(data, 0, data.Length);
    }

    static string ProcessParameters(string message) 
    {
        string artist_back;
        string title_back;
        try
        {
            using (MySqlConnection conn = new MySqlConnection(connectionString))
            {
                conn.Open();
              
                string[] words = message.Split('-');
                string artist = words[0];
                string title = words[1];
                artist_back = artist;
                title_back = title; 

                using (MySqlCommand cmd = new MySqlCommand("INSERT INTO songs (Artist, Title) VALUES (@artist, @title)", conn))
                {
                    cmd.Parameters.AddWithValue("@artist", artist);
                    cmd.Parameters.AddWithValue("@title", title);
                    cmd.ExecuteNonQuery();
                }
            }

            return artist_back + "-" + title_back;  //message to the android app
        }
        catch (Exception ex)
        {
            return "Error processing message: " + ex.Message; 
        }
    }

}
