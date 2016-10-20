__author__ = 'jan.zdunek'
# Copyright 2013-2015 Jan Zdunek
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import argparse
import codecs
import locale
import os.path

import xml.etree.cElementTree as cElementTree

from datetime import datetime
from datetime import timedelta


def parse_file_name_list(file_name_list):
    assert type(file_name_list) is list

    if file_name_list[-1].endswith('.xml'):
        return file_name_list, os.path.splitext(file_name_list[0])[0]+'.csv'
    else:
        return file_name_list[0:-1], file_name_list[-1]


def convert_time(t):
    seconds = t.seconds
    seconds += (t.microseconds / 1000000.0)
    return seconds


def get_keyword(kw_tag):
    name = kw_tag.attrib.get('name')
    name = name[name.rfind('=') + 1:].strip()
    return name


def calc_elapsed_time(status_tag):
    endtime = datetime.strptime(status_tag.attrib.get('endtime') + '000',
                                '%Y%m%d %H:%M:%S.%f')
    starttime = datetime.strptime(status_tag.attrib.get('starttime') + '000',
                                  '%Y%m%d %H:%M:%S.%f')
    return endtime - starttime


def analyse_output_xml(file_name_list):
    assert type(file_name_list) is list

    keywords = {}
    for file_name in file_name_list:
        tree = cElementTree.parse(file_name)
        root = tree.getroot()
        for kw in root.findall(".//kw"):
            name = get_keyword(kw)
            status = kw.find('./status')
            duration = calc_elapsed_time(status)
            if name in keywords:
                durations = keywords[name]
                durations.append(duration)
            else:
                durations = [duration]
            keywords.update({name: durations})
    return keywords


def evaluate_durations(keyword_data):
    keyword_information = {}
    for kw in keyword_data:
        durations = keyword_data[kw]
        number_of_durations = len(durations)
        duration = timedelta()
        for d in durations:
            duration += d
        average_duration = duration / number_of_durations
        keyword_information.update({kw: [number_of_durations, duration, average_duration]})
    return keyword_information


def create_output_line(keyword, no_of_occurrences, time_total, time_average, separator_character):
    return keyword + separator_character + str(no_of_occurrences) + separator_character + '{:n}'.format(
        convert_time(time_total)) + separator_character + '{:n}'.format(convert_time(time_average))


def profile(infile_name_list, outfile_name, file_encoding, separator_character, loc):
    keywords = analyse_output_xml(infile_name_list)
    keywords = evaluate_durations(keywords)

    default_locale = locale.getlocale()
    locale.setlocale(locale.LC_ALL, loc)
    output_file = codecs.open(outfile_name, 'w', encoding=file_encoding)
    output_file.write(
        'Keyword' + separator_character +
        'No of Occurrences' + separator_character +
        'Time Sum' + separator_character +
        'Time Avg' + '\n'
    )
    for kw in keywords:
        no_of_occurrences, time_sum, time_avg = keywords[kw]
        output_file.write(create_output_line(kw, no_of_occurrences, time_sum, time_avg, separator_character) + '\n')
    output_file.close()
    locale.setlocale(locale.LC_ALL, default_locale)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('file_name',
                        help='List of input files. If last file in list does not have xml as extension this file will be used as output file.',
                        nargs='+')
    parser.add_argument('-e',
                        '--encoding',
                        default='cp1252',
                        help='Encoding for output file.')
    parser.add_argument('-s',
                        '--separator',
                        default=';',
                        help='Separator used in output file.')
    parser.add_argument('-l',
                        '--locale',
                        default='',
                        help="Locale used for number formatting.")
    args = parser.parse_args()
    input_file_name_list, output_file_name = parse_file_name_list(args.file_name)
    separator = args.separator
    if separator == '\\t':
        separator = '\t'
    profile(input_file_name_list,
            output_file_name,
            args.encoding,
            separator,
            args.locale)