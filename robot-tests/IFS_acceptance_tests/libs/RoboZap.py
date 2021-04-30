import os
from zapv2 import ZAPv2 as ZAP
import time
import subprocess
from robot.api import logger
import base64
import uuid
import json
import requests
from datetime import datetime
import boto3


class RoboZap(object):
    ROBOT_LIBRARY_SCOPE = "GLOBAL"
    scan_id = ""
    context_id = ""
    def __init__(self):
        """
        ZAP Library can be imported with one argument

        Arguments:
            - ``proxy``: Proxy is required to initialize the ZAP Proxy at that location. This MUST include the port specification as well
            - ``port``: This is a portspecification that will be used across the suite


        Examples:

        | = Keyword Definition =  | = Description =  |

        | Library `|` RoboZap  | proxy | port |
        """
        self.zap = ZAP(proxies={"http": "http://127.0.0.1:8090/", "https": "http://127.0.0.1:8090/"})
        self.port = 8090

    def start_headless_zap(self):
        """
        Start OWASP ZAP without a GUI

        Examples:

        | start gui zap  | path | port |

        """
        try:
            cmd = "/zap/" + "zap.sh -daemon -dir /home/zap/.ZAP/ -config api.disablekey=true -port 8090".format(
                self.port
            )
            print(cmd)
            subprocess.Popen(cmd.split(" "), stdout=open(os.devnull, "w"))
            time.sleep(10)
        except IOError:
            print("ZAP Path is not configured correctly")

    def start_gui_zap(self):
        """
        Start OWASP ZAP with a GUI

        Examples:

        | start gui zap  | path | port |

        """
        try:
            cmd = "/zap/" + "zap.sh -config api.disablekey=true -port 8090".format(
                self.port
            )
            print(cmd)
            subprocess.Popen(cmd.split(" "), stdout=open(os.devnull, "w"))
            time.sleep(10)
        except IOError:
            print("ZAP Path is not configured correctly")

    def zap_open_url(self, url):
        """
        Invoke URLOpen with ZAP

        Examples:

        | zap open url  | target |

        """
        self.zap.urlopen(url)
        time.sleep(4)

    def zap_define_context(self):
        """
        Add Target to a context and use the context to perform all scanning/spidering operations

        Examples:

        | zap define context  | contextname  | target |

        """
        regex = "{0}.*".format("https://ifs-at-zaptest.apps.org-env-0.org.innovateuk.ukri.org/")
        print("logging context start")
        context_id = self.zap.context.new_context(contextname="test")
        time.sleep(1)
        self.zap.context.include_in_context("test", regex=regex)
        time.sleep(5)
        print(context_id)
        print(regex)
        print("logging context end")
        return context_id

    def zap_start_spider(self):
        """
        Start ZAP Spider with ZAP's inbuilt spider mode

        Examples:

        | zap start spider  | target  | url |

        """
        try:

            spider_id = self.zap.spider.scan(url="https://ifs-at-zaptest.apps.org-env-0.org.innovateuk.ukri.org/", contextname="test")
            time.sleep(5)
            print("spider id ",spider_id)
            return spider_id
        except Exception as e:
            print((e.message))

    def zap_spider_status(self, spider_id):
        """
        Fetches the status for the spider id provided by the user
        Examples:
        | zap spider status  | spider_id |
        """
        while int(self.zap.spider.status(spider_id)) < 100:
            logger.info(
                "Spider running at {0}%".format(int(self.zap.spider.status(spider_id)))
            )
            time.sleep(10)

    def zap_start_ascan(self, policy="Default Policy"):
        """
        Initiates ZAP Active Scan on the target url and context

        Examples:

        | zap start ascan  | context  | url |

        """
        print("print context Id, scan_id start")

        try:
            scan_id = self.zap.ascan.scan(
                contextid=1, url="https://ifs-at-zaptest.apps.org-env-0.org.innovateuk.ukri.org/", scanpolicyname=policy
            )
            time.sleep(5)

            print("print context Id, scan_id end")
            return scan_id
        except Exception as e:
            print(e.message)

    def zap_scan_status(self):
        """
        Fetches the status for the spider id provided by the user

        Examples:

        | zap scan status  | scan_id |

        """
        while int(self.zap.ascan.status(scan_id)) < 100:
            logger.info(
                "Scan running at {0}%".format(int(self.zap.ascan.status(scan_id)))
            )
            time.sleep(10)

    def zap_write_to_json_file(self):
        """

        Fetches all the results from zap.core.alerts() and writes to json file.

        Examples:

        | zap write to json  | scan_id |

        """
        print("writing json to a file")
        core = self.zap.core

        all_vuls = []
        for i, na in enumerate(core.alerts(baseurl="http://localhost:8090")):
            print("current iteration ")
            vul = {}
            vul["name"] = na["alert"]
            vul["confidence"] = na.get("confidence", "")
            if na.get("risk") == "High":
                vul["severity"] = 3
            elif na.get("risk") == "Medium":
                vul["severity"] = 2
            elif na.get("risk") == "Low":
                vul["severity"] = 1
            else:
                vul["severity"] = 0

            vul["cwe"] = na.get("cweid", 0)
            vul["uri"] = na.get("url", "")
            vul["param"] = na.get("param", "")
            vul["attack"] = na.get("attack", "")
            vul["evidence"] = na.get("evidence", "")
            message_id = na.get("messageId", "")
            message = core.message(message_id)
            if isinstance(message, dict):
                request = base64.b64encode(
                    "{0}{1}".format(message["requestHeader"], message["requestBody"])
                )
                response = base64.b64encode(
                    "{0}{1}".format(message["responseHeader"], message["responseBody"])
                )
                vul["request"] = request
                vul["response"] = response
                vul["rtt"] = int(message["rtt"])

            all_vuls.append(vul)

        print("json string is ")

        filename = "{0}.json".format(str(uuid.uuid4()))
        with open(filename, "w") as json_file:
            json_file.write(json.dumps(all_vuls))

        return filename

    def zap_write_to_orchy(self, report_file, secret, access, hook_uri):
        """
                Generates an XML Report and writes said report to orchestron over a webhook.

                Mandatory Fields:
                - Report_file: Absolute Path of Report File - JSON or XML
                - Token: Webhook Token
                - hook_uri: the unique URI to post the XML Report to

                Examples:

                | zap write to orchy  | report_file_path | token | hook_uri

        """
        # xml_report = self.zap.core.xmlreport()
        # with open('zap_scan.xml','w') as zaprep:
        #     zaprep.write(xml_report)
        try:
            files = {"file": open(report_file, "rb")}
            auth = {"Secret-Key": secret, "Access-Key": access}
            r = requests.post(hook_uri, headers=auth, files=files)
            if r.status_code == 200:
                return "Successfully posted to Orchestron"
            else:
                raise Exception("Unable to post successfully")
        except Exception as e:
            print(e)

    def zap_export_report(
        self
    ):
        """
        This functionality works on ZAP 2.7.0 only. It leverages the Export Report Library to generate a report.
        Currently ExportReport doesnt have an API endpoint in python. We will be using the default ZAP REST API for this

        :param export_file: location to which the export needs to happen. Absolute path with the export file name and extension
        :param export_format: file extension of the exported file. Can be XML, JSON, HTML, PDF, DOC
        :param report_title: Title of the exported report
        :param report_author: Name of the Author of the report
        Examples:

        | zap export report | export_path | export_format |

        """

        url = "http://localhost:8090/JSON/exportreport/action/generate/".format(
            self.port
        )
        export_path = "/zap/zapreport.json"
        extension = json
        report_time = datetime.now().strftime("%I:%M%p on %B %d, %Y")
        source_info = "{0};{1};ZAP Team;{2};{3};v1;v1;{4}".format(
            "ZAP report", "Arun", report_time, report_time, "ZAP report"
        )
        alert_severity = "t;t;t;t"  # High;Medium;Low;Info
        alert_details = "t;t;t;t;t;t;t;t;t;t"  # CWEID;#WASCID;Description;Other Info;Solution;Reference;Request Header;Response Header;Request Body;Response Body
        data = {
            "absolutePath": export_path,
            "fileExtension": extension,
            "sourceDetails": source_info,
            "alertSeverity": alert_severity,
            "alertDetails": alert_details,
        }

        r = requests.post(url, data=data)
        if r.status_code == 200:
            pass
        else:
            raise Exception("Unable to generate report")

    def zap_write_to_s3_bucket(self, filename, bucket_name):
        s3 = boto3.client("s3")
        outfile_name = "ZAP-RESULT-{}.json".format(str(uuid.uuid4()))
        s3.upload_file(filename, bucket_name, outfile_name)
        logger.warn("Filename uploaded to S3 is: {}".format(outfile_name))

    def retrieve_secret_from_ssm(self, secret, region="us-west-2", decrypt=True):
        db = boto3.client("ssm", region_name=region)
        param = db.get_parameter(Name=secret, WithDecryption=decrypt)["Parameter"][
            "Value"
        ]
        return param

    def zap_load_script(
        self,
        script_name,
        script_type,
        script_engine,
        script_file,
        desc="Generic Description of a ZAP Script",
    ):
        """
        :param script_name:
        :param script_type:
        :param script_engine:
        :param script_file:
        :param desc:
        :return:
        """
        zap_script_status = self.zap.script.load(
            scriptname=script_name,
            scripttype=script_type,
            scriptengine=script_engine,
            filename=script_file,
            scriptdescription=desc,
        )
        logger.info(zap_script_status)

    def zap_run_standalone_script(self, script_name):
        zap_script_run_status = self.zap.script.run_stand_alone_script(script_name)
        logger.info(zap_script_run_status)

    def zap_shutdown(self):
        """
        Shutdown process for ZAP Scanner
        """
        self.zap.core.shutdown()