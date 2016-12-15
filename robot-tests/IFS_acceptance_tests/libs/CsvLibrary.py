import csv

def read_csv_file(csv_file_name):
  f = open(csv_file_name, 'r')
  csv_file = csv.reader(f)
  csv_data = []
  for row in csv_file:
    csv_data.append(row)
  f.close()
  return csv_data

