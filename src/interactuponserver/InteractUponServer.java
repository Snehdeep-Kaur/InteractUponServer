package interactuponserver;

import com.vmm.JHTTPServer;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.Random;
import javax.swing.JOptionPane;

public class InteractUponServer extends JHTTPServer {

    public InteractUponServer(int port) throws IOException {
        super(port);
    }

    @Override
    public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {

        Response res = new Response(HTTP_OK, "text/plain", "hello from server");
        System.out.println(uri);

        if (uri.contains("GetResource")) {

            uri = uri.substring(1);
            uri = uri.substring(uri.indexOf("/") + 1);
            res = sendCompleteFile(uri);

        } else if (uri.contains("sendname")) {

            String name = parms.getProperty("name");
            res = new Response(HTTP_OK, "text/plain", "name is " + name);
        } else if (uri.contains("Adminlogin")) {
            String username = parms.getProperty("username");
            String password = parms.getProperty("password");

            //Database code
            String ans = "";
            try {

                ResultSet rs = DBLoader.executeStatement("select * from admin_login where username='" + username + "' and password='" + password + "'");
                if (rs.next()) {
                    ans = "success";
                } else {
                    ans = "fail";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            res = new Response(HTTP_OK, "text/plain", ans);

        } else if (uri.contains("Teacherlogin")) {
            String teacherid = parms.getProperty("teacherid");
            String password = parms.getProperty("password");

            //Database code
            String ans = "";
            try {
                int id = Integer.parseInt(teacherid);
                ResultSet rs = DBLoader.executeStatement("select * from teachers where teacherid=" + id + " and tpassword='" + password + "'");
                if (rs.next()) {
                    ans = "success";
                } else {
                    ans = "fail";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            res = new Response(HTTP_OK, "text/plain", ans);

        } else if (uri.contains("Studentlogin")) {
            String sid = parms.getProperty("studentid");
            String password = parms.getProperty("password");

            //Database code
            String ans = "";
            try {
                int id = Integer.parseInt(sid);
                System.out.println(id);
                ResultSet rs = DBLoader.executeStatement("select * from students where studentid=" + id + " and spassword='" + password + "'");
                if (rs.next()) {
                    ans = "success";
                } else {
                    ans = "fail";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            res = new Response(HTTP_OK, "text/plain", ans);

        } else if (uri.contains("signup")) {
            String username = parms.getProperty("username");
            String password = parms.getProperty("password");
            String email = parms.getProperty("email");

            String ans = "";

            try {

                ResultSet rs = DBLoader.executeStatement("select * from adminlogin where username='" + username + "'");
                if (rs.next()) {

                    ans = "fail";

                } else {

                    String filename = saveFileOnServerWithRandomName(files, parms, "photo", "src/uploads");
                    String filepath = "src/uploads/" + filename;

                    rs.moveToInsertRow();
                    rs.updateString("username", username);
                    rs.updateString("password", password);
                    rs.updateString("email", email);
                    rs.updateString("photo", filepath);
                    rs.insertRow();
                    ans = "success";

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            res = new Response(HTTP_OK, "text/plain", ans);

        } else if (uri.contains("adddepartment")) {
            String cbdept = parms.getProperty("cbdept");
            String hodname = parms.getProperty("hodname");
            String phnno = parms.getProperty("phnno");
            String email = parms.getProperty("email");
            String desp = parms.getProperty("desp");

            //Database code
            String ans = "";
            try {

                ResultSet rs = DBLoader.executeStatement("select * from department where departmentname='" + cbdept + "'");
                if (rs.next()) {
                    ans = "fail";

                } else {
                    ans = "success";
                    rs.moveToInsertRow();
                    rs.updateString("departmentname", cbdept);
                    rs.updateString("hodname", hodname);
                    rs.updateString("phoneno", phnno);
                    rs.updateString("email", email);
                    rs.updateString("description", desp);
                    rs.insertRow();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            res = new Response(HTTP_OK, "text/plain", ans);

        } else if (uri.contains("managedepartment")) {
            String data = "";
            try {

                ResultSet rs = DBLoader.executeStatement("select* from department");
                while (rs.next()) {
                    try {
                        String departmentname = rs.getString("departmentname");
                        String hodname = rs.getString("hodname");
                        String phoneno = rs.getString("phoneno");
                        String email = rs.getString("email");
                        String description = rs.getString("description");
                        data = data + departmentname + ":#" + hodname + ":#" + phoneno + ":#" + email + ":#" + description + "&";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", data);

        } else if (uri.contains("deletedepartment")) {
            String dn = parms.getProperty("departmentname");
            String ans = "";

            try {
                ResultSet rs = DBLoader.executeStatement("select * from department where departmentname= '" + dn + "'");
                if (rs.next()) {
                    rs.deleteRow();
                    ans = "success";

                } else {
                    ans = "fail";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            res = new Response(HTTP_OK, "text/plain", ans);

        } else if (uri.contains("getdepartmentnames")) {
            String data = "";
            try {

                ResultSet rs = DBLoader.executeStatement("select departmentname from department");
                while (rs.next()) {
                    try {
                        String departmentname = rs.getString("departmentname");
                        data = data + departmentname + ":&";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", data);

        } else if (uri.contains("addcourse")) {
            String cname = parms.getProperty("coursename");
            String dname = parms.getProperty("departmentname");
            String sem = parms.getProperty("semester");
            String desc = parms.getProperty("description");

            //Database code
            String ans = "";
            try {

                ResultSet rs = DBLoader.executeStatement("select * from courses");
                ResultSet rs1 = DBLoader.executeStatement("select * from courses where coursename='" + cname + "' and semester ='" + sem + "'");
                if (rs1.next()) {
                    ans = "fail";
                } else {
                    ans = "success";
                    rs.moveToInsertRow();
                    rs.updateString("coursename", cname);
                    rs.updateString("departmentname", dname);
                    rs.updateString("semester", sem);
                    rs.updateString("description", desc);
                    rs.insertRow();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            res = new Response(HTTP_OK, "text/plain", ans);

        } else if (uri.contains("managecourse")) {
            String data = "";
            try {

                ResultSet rs = DBLoader.executeStatement("select* from courses");
                while (rs.next()) {
                    try {
                        String courseid = rs.getString("courseid");
                        String coursename = rs.getString("coursename");
                        String departmentname = rs.getString("departmentname");
                        String semester = rs.getString("semester");
                        String description = rs.getString("description");
                        data = data + courseid + ":#" + coursename + ":#" + departmentname + ":#" + semester + ":#" + description + "&";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", data);

        } else if (uri.contains("deletecourse")) {
            String cid = parms.getProperty("cid");
            String ans = "";

            try {
                ResultSet rs = DBLoader.executeStatement("select * from courses where courseid= " + cid + "");
                if (rs.next()) {
                    rs.deleteRow();
                    ans = "success";

                } else {
                    ans = "fail";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            res = new Response(HTTP_OK, "text/plain", ans);

        } else if (uri.contains("addteachers")) {
            String tname = parms.getProperty("teachername");
            String fname = parms.getProperty("tfathername");
            String phn = parms.getProperty("tphoneno");
            String em = parms.getProperty("temail");
            String qual = parms.getProperty("tqualification");
            String dname = parms.getProperty("departmentname");
            String addr = parms.getProperty("taddress");
            String pass = parms.getProperty("tpassword");
            //String photo = parms.getProperty("tphoto");

            String ans = "";
            try {
                String filename = saveFileOnServerWithRandomName(files, parms, "tphoto", "src/uploads");
                String filepath = "src/uploads/" + filename;
                ResultSet rs = DBLoader.executeStatement("select * from teachers");

                ans = "success";
                rs.moveToInsertRow();
                rs.updateString("tname", tname);
                rs.updateString("tfathername", fname);
                rs.updateString("tphoneno", phn);
                rs.updateString("temail", em);
                rs.updateString("tqualification", qual);
                rs.updateString("departmentname", dname);
                rs.updateString("taddress", addr);
                rs.updateString("tpassword", pass);
                rs.updateString("tphoto", filepath);
                rs.insertRow();

            } catch (Exception e) {
                e.printStackTrace();
            }

            res = new Response(HTTP_OK, "text/plain", ans);

        } else if (uri.contains("manageteachers")) {
            String data = "";
            try {
                String dname = parms.getProperty("departmentname");
                ResultSet rs;
                if (dname.equals("All")) {
                    rs = DBLoader.executeStatement("select * from teachers");
                } else {
                    rs = DBLoader.executeStatement("select * from teachers where departmentname ='" + dname + "'");
                }

                while (rs.next()) {
                    try {
                        String teacherid = rs.getString("teacherid");
                        String tname = rs.getString("tname");
                        String tfathername = rs.getString("tfathername");
                        String tphoneno = rs.getString("tphoneno");
                        String temail = rs.getString("temail");
                        String tqualification = rs.getString("tqualification");
                        String departmentname = rs.getString("departmentname");
                        String taddress = rs.getString("taddress");
                        String tpassword = rs.getString("tpassword");
                        String tphoto = rs.getString("tphoto");
                        data = data + teacherid + ":#" + tname + ":#" + tfathername + ":#" + tphoneno + ":#" + temail + ":#" + tqualification + ":#" + departmentname + ":#" + taddress + ":#" + tpassword + ":#" + tphoto + "&";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", data);

        } else if (uri.contains("deleteteacher")) {
            String tid = parms.getProperty("teacherid");
            String ans = "";

            try {
                ResultSet rs = DBLoader.executeStatement("select * from teachers where teacherid= " + tid + "");
                if (rs.next()) {
                    rs.deleteRow();
                    ans = "success";

                } else {
                    ans = "fail";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            res = new Response(HTTP_OK, "text/plain", ans);

        } else if (uri.contains("getcoursenames")) {
            String dn = parms.getProperty("departmentname");
            String data = "";
            try {
                ResultSet rs = DBLoader.executeStatement("select distinct(coursename) from courses where departmentname = '" + dn + "'");
                while (rs.next()) {
                    try {
                        String coursename = rs.getString("coursename");
                        data = data + coursename + ":&";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", data);

        } else if (uri.contains("getsemesters")) {
            String dn = parms.getProperty("departmentname");
            String cn = parms.getProperty("coursename");
            String data = "";
            try {
                ResultSet rs = DBLoader.executeStatement("select distinct(semester) from courses where departmentname = '" + dn + "' and coursename ='" + cn + "'");
                while (rs.next()) {
                    try {
                        String semester = rs.getString("semester");
                        data = data + semester + ":&";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", data);

        } else if (uri.contains("addstudent")) {
            String dname = parms.getProperty("departmentname");
            String cname = parms.getProperty("coursename");
            String sem = parms.getProperty("semester");
            String roll = parms.getProperty("rollno");
            String name = parms.getProperty("name");
            String fname = parms.getProperty("fathername");
            String dob = parms.getProperty("dob");
            String phn = parms.getProperty("phoneno");
            String em = parms.getProperty("email");
            String addr = parms.getProperty("address");
            String ans = "";
            String cid = "";
            try {
                String filename = saveFileOnServerWithRandomName(files, parms, "photo", "src/uploads");
                String filepath = "src/uploads/" + filename;
                ResultSet rs = DBLoader.executeStatement("select * from students");
                ResultSet rs1 = DBLoader.executeStatement("select courseid from courses where departmentname = '" + dname + "' and coursename = '" + cname + "' and semester = '" + sem + "'");
                if (rs1.next()) {
                    cid = rs1.getString("courseid");
                }
                int cidd = Integer.parseInt(cid);
                ans = "success";
                rs.moveToInsertRow();
                rs.updateString("srollno", roll);
                rs.updateString("sname", name);
                rs.updateString("sfname", fname);
                rs.updateString("sdob", dob);
                rs.updateString("sphoneno", phn);
                rs.updateString("semail", em);
                rs.updateString("saddress", addr);
                rs.updateString("sphoto", filepath);
                rs.updateString("departmentname", dname);
                rs.updateInt("courseid", cidd);
                rs.updateString("semester", sem);
                rs.insertRow();
            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", ans);

        } else if (uri.contains("managestudents")) {
            String data = "";
            String cid = "";
            try {
                String dname = parms.getProperty("departmentname");
                String cname = parms.getProperty("coursename");
                String sem = parms.getProperty("semester");
                ResultSet rs, rs1;

                rs1 = DBLoader.executeStatement("select courseid from courses where departmentname = '" + dname + "' and coursename = '" + cname + "' and semester = '" + sem + "'");
                if (rs1.next()) {
                    cid = rs1.getString("courseid");
                }
                int cidd = Integer.parseInt(cid);
                rs = DBLoader.executeStatement("select * from students where departmentname ='" + dname + "' and courseid =" + cidd + " and semester ='" + sem + "'");
                while (rs.next()) {
                    try {
                        String studentid = rs.getString("studentid");
                        String srollno = rs.getString("srollno");
                        String sname = rs.getString("sname");
                        String sfname = rs.getString("sfname");
                        String sdob = rs.getString("sdob");
                        String sphoneno = rs.getString("sphoneno");
                        String semail = rs.getString("semail");
                        String saddress = rs.getString("saddress");
                        String sphoto = rs.getString("sphoto");
                        String departmentname = rs.getString("departmentname");
                        String courseid = rs.getString("courseid");
                        String semester = rs.getString("semester");
                        String spassword = rs.getString("spassword");

                        data = data + studentid + ":#" + srollno + ":#" + sname + ":#" + sfname + ":#" + sdob + ":#" + sphoneno + ":#" + semail + ":#" + saddress + ":#" + sphoto + ":#" + departmentname + ":#" + courseid + ":#" + semester + ":#" + spassword + "&";
                        System.out.println(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", data);

        } else if (uri.contains("deletestudent")) {
            String sid = parms.getProperty("studentid");
            String ans = "";

            try {
                ResultSet rs = DBLoader.executeStatement("select * from students where studentid= " + sid + "");
                if (rs.next()) {
                    rs.deleteRow();
                    ans = "success";

                } else {
                    ans = "fail";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            res = new Response(HTTP_OK, "text/plain", ans);

        } else if (uri.contains("getTeacherProfile")) {
            String tid = parms.getProperty("teacherid");
            String data = "";
            try {
                int id = Integer.parseInt(tid);
                ResultSet rs = DBLoader.executeStatement("select* from teachers where teacherid = " + id + "");
                while (rs.next()) {
                    try {
                        String teacherid = rs.getString("teacherid");
                        String tname = rs.getString("tname");
                        String pn = rs.getString("tphoneno");
                        String email = rs.getString("temail");
                        String qual = rs.getString("tqualification");
                        String dept = rs.getString("departmentname");
                        String addr = rs.getString("taddress");
                        String photo = rs.getString("tphoto");
                        data = data + teacherid + ":#" + tname + ":#" + pn + ":#" + email + ":#" + qual + ":#" + dept + ":#" + addr + ":#" + photo;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", data);

        } else if (uri.contains("getStudentProfile")) {
            String sid = parms.getProperty("studentid");
            String data = "";
            String cn = "";
            try {
                int id = Integer.parseInt(sid);
                ResultSet rs = DBLoader.executeStatement("select* from students where studentid = " + id + "");
                while (rs.next()) {
                    try {

                        String studentid = rs.getString("studentid");
                        String roll = rs.getString("srollno");
                        String sname = rs.getString("sname");
                        String sfname = rs.getString("sfname");
                        String dob = rs.getString("sdob");
                        String pn = rs.getString("sphoneno");
                        String email = rs.getString("semail");
                        String dept = rs.getString("departmentname");
                        String cid = rs.getString("courseid");
                        String sem = rs.getString("semester");
                        String addr = rs.getString("saddress");
                        String photo = rs.getString("sphoto");
                        int ccid = Integer.parseInt(cid);
                        ResultSet rs1 = DBLoader.executeStatement("select * from courses where courseid=" + ccid + "");
                        while (rs1.next()) {
                            cn = rs1.getString("coursename");
                        }
                        data = data + studentid + ":#" + roll + ":#" + sname + ":#" + sfname + ":#" + dob + ":#" + pn + ":#" + email + ":#" + dept + ":#" + cn + ":#" + sem + ":#" + addr + ":#" + photo;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", data);

        } else if (uri.contains("changeTeacherPassword")) {
            String tid = parms.getProperty("teacherid");
            String opf = parms.getProperty("tpassword");
            String npf = parms.getProperty("newpass");
            String ans = "";
            try {
                int id = Integer.parseInt(tid);
                ResultSet rs = DBLoader.executeStatement("select * from teachers where teacherid=" + id + "");
                if (rs.next()) {
                    try {
                        String pass = rs.getString("tpassword");
                        if (pass.equals(opf)) {
                            ans = "success";

                            rs.updateString("tpassword", npf);
                            rs.updateRow();
                        } else {
                            ans = "fail";
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", ans);

        } else if (uri.contains("uploadassignment")) {
            String cid = "";
            String ans = "";
            try {
                String tid = parms.getProperty("tid");
                String dname = parms.getProperty("departmentname");
                String cname = parms.getProperty("coursename");
                String sem = parms.getProperty("semester");
                String title = parms.getProperty("title");
                String details = parms.getProperty("detail");
                String sd = parms.getProperty("submissiondate");
                String ad = parms.getProperty("assignmentdate");
                System.out.println("abc" + sem);
                ResultSet rs, rs1;
                String filename = saveFileOnServerWithRandomName(files, parms, "file", "src/uploads");
                String filepath = "src/uploads/" + filename;
                rs1 = DBLoader.executeStatement("select courseid from courses where departmentname = '" + dname + "' and coursename = '" + cname + "' and semester = '" + sem + "'");
                if (rs1.next()) {
                    cid = rs1.getString("courseid");
                }
                int cidd = Integer.parseInt(cid);
                rs = DBLoader.executeStatement("select * from assignments");
                ans = "success";
                rs.moveToInsertRow();
                rs.updateString("tid", tid);
                rs.updateInt("courseid", cidd);
                rs.updateString("semester", sem);
                rs.updateString("title", title);
                rs.updateString("detail", details);
                rs.updateString("file", filepath);
                rs.updateString("submissiondate", sd);
                rs.updateString("assignmentdate", ad);

                rs.insertRow();
            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", ans);

        } else if (uri.contains("changeStudentPassword")) {
            String sid = parms.getProperty("studentid");
            String opf = parms.getProperty("spassword");
            String npf = parms.getProperty("newpass");
            String ans = "";
            try {
                int id = Integer.parseInt(sid);
                ResultSet rs = DBLoader.executeStatement("select * from students where studentid=" + id + "");
                if (rs.next()) {
                    try {
                        String pass = rs.getString("spassword");
                        if (pass.equals(opf)) {
                            ans = "success";
                            rs.updateString("spassword", npf);
                            rs.updateRow();
                        } else {
                            ans = "fail";
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", ans);

        } else if (uri.contains("manageuploadedassignments")) {
            String data = "";
            String cid = "";
            try {
                String tid = parms.getProperty("teacherid");
                String cn = parms.getProperty("coursename");
                String sem = parms.getProperty("semester");
                String dept = parms.getProperty("departmentname");
                ResultSet rs, rs1, rs2;

                rs1 = DBLoader.executeStatement("select courseid from courses where departmentname = '" + dept + "' and coursename = '" + cn + "' and semester = '" + sem + "'");
                if (rs1.next()) {
                    cid = rs1.getString("courseid");
                }
                //int id = Integer.parseInt(cid);
                int t = Integer.parseInt(tid);
                rs = DBLoader.executeStatement("select * from assignments where tid =" + t + " and courseid =" + cid + " and semester ='" + sem + "' order by assignmentdate desc");

                while (rs.next()) {
                    try {
                        String aid = rs.getString("aid");
                        String ttid = rs.getString("tid");
                        String ccid = rs.getString("courseid");
                        String semester = rs.getString("semester");
                        String title = rs.getString("title");
                        String detail = rs.getString("detail");
                        String file = rs.getString("file");
                        String submissiondate = rs.getString("submissiondate");
                        String assignmentdate = rs.getString("assignmentdate");

                        data = data + aid + ":#" + ttid + ":#" + cn + ":#" + semester + ":#" + title + ":#" + detail + ":#" + file + ":#" + submissiondate + ":#" + assignmentdate + "&";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", data);

        } else if (uri.contains("deleteassignment")) {
            String aid = parms.getProperty("aid");
            System.out.println(aid);
            String ans = "";
            int id = Integer.parseInt(aid);
            try {
                ResultSet rs = DBLoader.executeStatement("select * from assignments where aid= " + id + "");
                if (rs.next()) {
                    rs.deleteRow();
                    ans = "success";

                } else {
                    ans = "fail";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            res = new Response(HTTP_OK, "text/plain", ans);

        } else if (uri.contains("getassignments")) {
            String dn = parms.getProperty("departmentname");
            String cn = parms.getProperty("coursename");
            String sem = parms.getProperty("semester");
            String data = "";
            String cid = "";
            try {
                ResultSet rs, rs1, rs2;

                rs1 = DBLoader.executeStatement("select courseid from courses where departmentname = '" + dn + "' and coursename = '" + cn + "' and semester = '" + sem + "'");
                if (rs1.next()) {
                    cid = rs1.getString("courseid");
                }
                int id = Integer.parseInt(cid);
                rs = DBLoader.executeStatement("select * from assignments where courseid =" + id + " and semester ='" + sem + "' order by assignmentdate desc");
                String tn = "";
                while (rs.next()) {
                    try {
                        String aid = rs.getString("aid");
                        String tid = rs.getString("tid");
                        String courseid = rs.getString("courseid");
                        String semester = rs.getString("semester");
                        String title = rs.getString("title");
                        String detail = rs.getString("detail");
                        String file = rs.getString("file");
                        String submissiondate = rs.getString("submissiondate");
                        String assignmentdate = rs.getString("assignmentdate");
                        int ttid = Integer.parseInt(tid);
                        rs2 = DBLoader.executeStatement("select * from teachers where teacherid =" + ttid + "");
                        while (rs2.next()) {
                            tn = rs2.getString("tname");
                        }
                        data = data + ":#" + aid + ":#" + tn + ":#" + courseid + ":#" + semester + ":#" + title + ":#" + detail + ":#" + file + ":#" + submissiondate + ":#" + assignmentdate + "&";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", data);

        } else if (uri.contains("getcompleteassignmentdetails")) {
            String id = parms.getProperty("aid");
            String tname = parms.getProperty("tname");
            String data = "";
            try {
                ResultSet rs;
                int aid = Integer.parseInt(id);
                rs = DBLoader.executeStatement("select * from assignments where aid =" + aid + "");
                while (rs.next()) {
                    try {
                        String aaid = rs.getString("aid");
                        String tid = rs.getString("tid");
                        String courseid = rs.getString("courseid");
                        String semester = rs.getString("semester");
                        String title = rs.getString("title");
                        String detail = rs.getString("detail");
                        String file = rs.getString("file");
                        String submissiondate = rs.getString("submissiondate");
                        String assignmentdate = rs.getString("assignmentdate");
                        data = data + ":#" + aid + ":#" + tname + ":#" + courseid + ":#" + semester + ":#" + title + ":#" + detail + ":#" + file + ":#" + submissiondate + ":#" + assignmentdate;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", data);

        } else if (uri.contains("checksubmission")) {
            String aaid = parms.getProperty("aid");
            String ssid = parms.getProperty("sid");
            String ans = "";

            try {
                int aid = Integer.parseInt(aaid);
                int sid = Integer.parseInt(ssid);
                ResultSet rs = DBLoader.executeStatement("select * from submitassignment where aid =" + aid + " and sid= " + sid + "");
                if (rs.next()) {
                    ans = "success";
                } else {
                    ans = "fail";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            res = new Response(HTTP_OK, "text/plain", ans);

        } else if (uri.contains("submitassignments")) {
            String aaid = parms.getProperty("aid");
            String ssid = parms.getProperty("sid");
            String date = parms.getProperty("date");
            String ans = "";

            try {
                String filename = saveFileOnServerWithRandomName(files, parms, "file", "src/uploads");
                String filepath = "src/uploads/" + filename;
                int aid = Integer.parseInt(aaid);
                int sid = Integer.parseInt(ssid);
                ResultSet rs = DBLoader.executeStatement("select * from submitassignment");
                rs.moveToInsertRow();
                rs.updateInt("aid", aid);
                rs.updateString("file", filepath);
                rs.updateInt("sid", sid);
                rs.updateString("date", date);
                rs.insertRow();
                ans = "success";

            } catch (Exception e) {
                e.printStackTrace();
            }

            res = new Response(HTTP_OK, "text/plain", ans);

        } else if (uri.contains("getsubmittedassignments")) {
            String id = parms.getProperty("aid");
            String data = "";
            try {
                ResultSet rs, rs1;
                int aid = Integer.parseInt(id);
                rs = DBLoader.executeStatement("select * from submitassignment where aid =" + aid + "");
                String roll = "";
                while (rs.next()) {
                    try {
                        String said = rs.getString("said");
                        String aaid = rs.getString("aid");
                        String file = rs.getString("file");
                        String sid = rs.getString("sid");
                        String date = rs.getString("date");
                        int a = Integer.parseInt(sid);
                        rs1 = DBLoader.executeStatement("select * from students where studentid =" + a + "");
                        while (rs1.next()) {
                            roll = rs1.getString("srollno");
                        }
                        data = data + sid + ":#" + roll + ":#" + file + ":#" + date + "&";
                        System.out.println("aaaa" + data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", data);

        } else if (uri.contains("postnotification")) {
            String cn = parms.getProperty("coursename");
            String dn = parms.getProperty("departmentname");
            String sem = parms.getProperty("semester");
            String tid = parms.getProperty("tid");
            String msg = parms.getProperty("message");
            String date = parms.getProperty("date");
            String time = parms.getProperty("time");
            String ans = "";

            try {
                int id = Integer.parseInt(tid);
                String cid = "";
                ResultSet rs = DBLoader.executeStatement("select * from courses where departmentname = '" + dn + "' and coursename = '" + cn + "' and semester = '" + sem + "'");
                while (rs.next()) {
                    cid = rs.getString("courseid");
                }
                int ccid = Integer.parseInt(cid);
                ResultSet rs1 = DBLoader.executeStatement("select * from notifications");
                rs1.moveToInsertRow();
                rs1.updateInt("tid", id);
                rs1.updateInt("courseid", ccid);
                rs1.updateString("semester", sem);
                rs1.updateString("departmentname", dn);
                rs1.updateString("message", msg);
                rs1.updateString("date", date);
                rs1.updateString("time", time);
                rs1.insertRow();
                ans = "success";

            } catch (Exception e) {
                e.printStackTrace();
            }

            res = new Response(HTTP_OK, "text/plain", ans);

        } else if (uri.contains("getallnotifications")) {
            String tid = parms.getProperty("tid");
            String dn = parms.getProperty("departmentname");
            String cn = parms.getProperty("coursename");
            String sem = parms.getProperty("semester");
            String data = "";

            try {
                int id = Integer.parseInt(tid);
                String tname = "";
                String cid = "";
                ResultSet rs = DBLoader.executeStatement("select * from teachers where teacherid=" + id + "");
                while (rs.next()) {
                    tname = rs.getString("tname");
                }
                ResultSet rs2 = DBLoader.executeStatement("select * from courses where departmentname = '" + dn + "' and coursename = '" + cn + "' and semester = '" + sem + "'");
                if (rs2.next()) {
                    cid = rs2.getString("courseid");
                }
                int course = Integer.parseInt(cid);
                System.out.println(course);
                ResultSet rs1 = DBLoader.executeStatement("select * from notifications where tid=" + id + " and courseid=" + course + "");
                while (rs1.next()) {
                    String mm = rs1.getString("message");
                    String time = rs1.getString("time");
                    String date = rs1.getString("date");
                    data = data + mm + "###" + tname + "###" + time + "###" + date + "&&";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(data);
            res = new Response(HTTP_OK, "text/plain", data);

        } else if (uri.contains("getstudentnotifications")) {
            String dn = parms.getProperty("departmentname");
            String cn = parms.getProperty("coursename");
            String sem = parms.getProperty("semester");
            String data = "";
            try {
                String cid = "";
                ResultSet rs = DBLoader.executeStatement("select * from courses where departmentname = '" + dn + "' and coursename = '" + cn + "' and semester = '" + sem + "'");
                while (rs.next()) {
                    cid = rs.getString("courseid");
                }
                int ccid = Integer.parseInt(cid);
                ResultSet rs1 = DBLoader.executeStatement("select * from notifications where courseid=" + ccid + "");
                while (rs1.next()) {
                    String tname;
                    String tid = rs1.getString("tid");
                    int id = Integer.parseInt(tid);
                    ResultSet rs2 = DBLoader.executeStatement("select * from teachers where teacherid=" + id + "");
                    while (rs2.next()) {
                        tname = rs2.getString("tname");
                        String mm = rs1.getString("message");
                        String time = rs1.getString("time");
                        String date = rs1.getString("date");
                        data = data + mm + "###" + tname + "###" + time + "###" + date + "&&";
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(data);
            res = new Response(HTTP_OK, "text/plain", data);

        } else if (uri.contains("uploadnotes")) {
            String cid = "";
            String ans = "";
            try {
                String title = parms.getProperty("title");
                String details = parms.getProperty("detail");
                String type = parms.getProperty("type");
                String tid = parms.getProperty("tid");
                String dname = parms.getProperty("departmentname");
                String cname = parms.getProperty("coursename");
                String sem = parms.getProperty("semester");
                String date = parms.getProperty("date");
                System.out.println("abc" + sem);
                ResultSet rs, rs1;
                String filename = saveFileOnServerWithRandomName(files, parms, "file", "src/uploads");
                String filepath = "src/uploads/" + filename;
                rs1 = DBLoader.executeStatement("select courseid from courses where departmentname = '" + dname + "' and coursename = '" + cname + "' and semester = '" + sem + "'");
                if (rs1.next()) {
                    cid = rs1.getString("courseid");
                }
                int cidd = Integer.parseInt(cid);
                rs = DBLoader.executeStatement("select * from notes");
                ans = "success";
                rs.moveToInsertRow();
                rs.updateString("title", title);
                rs.updateString("detail", details);
                rs.updateString("type", type);
                rs.updateString("file", filepath);
                rs.updateString("tid", tid);
                rs.updateInt("cid", cidd);
                rs.updateString("dept", dname);
                rs.updateString("sem", sem);
                rs.updateString("date", date);
                rs.insertRow();
            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", ans);

        } else if (uri.contains("manageuploadednotes")) {
            String data = "";
            String cid = "";
            try {
                String tid = parms.getProperty("teacherid");
                String cn = parms.getProperty("coursename");
                String sem = parms.getProperty("semester");
                String dept = parms.getProperty("departmentname");
                ResultSet rs, rs1, rs2;

                rs1 = DBLoader.executeStatement("select courseid from courses where departmentname = '" + dept + "' and coursename = '" + cn + "' and semester = '" + sem + "'");
                if (rs1.next()) {
                    cid = rs1.getString("courseid");
                }
                int id = Integer.parseInt(cid);
                int t = Integer.parseInt(tid);
                rs = DBLoader.executeStatement("select * from notes where tid =" + t + " and cid =" + id + " and sem ='" + sem + "' order by date desc");

                while (rs.next()) {
                    try {
                        String nid = rs.getString("noteid");
                        String title = rs.getString("title");
                        String detail = rs.getString("detail");
                        String type = rs.getString("type");
                        String file = rs.getString("file");
                        //String ttid = rs.getString("tid");
                        String ccid = rs.getString("cid");
                        //String dname = rs.getString("dept");
                        String semester = rs.getString("sem");
                        String date = rs.getString("date");

                        data = data + nid + ":#" + title + ":#" + detail + ":#" + type + ":#" + file + ":#" + ccid + ":#" + semester + ":#" + date + "&";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", data);

        } else if (uri.contains("deletenotes")) {
            String nid = parms.getProperty("noteid");
            String ans = "";
            int id = Integer.parseInt(nid);
            try {
                ResultSet rs = DBLoader.executeStatement("select * from notes where noteid= " + id + "");
                if (rs.next()) {
                    rs.deleteRow();
                    ans = "success";

                } else {
                    ans = "fail";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            res = new Response(HTTP_OK, "text/plain", ans);

        } else if (uri.contains("getallnotes")) {
            String dn = parms.getProperty("departmentname");
            String cn = parms.getProperty("coursename");
            String sem = parms.getProperty("semester");
            String data = "";
            String cid = "";
            try {
                ResultSet rs, rs1, rs2;

                rs1 = DBLoader.executeStatement("select courseid from courses where departmentname = '" + dn + "' and coursename = '" + cn + "' and semester = '" + sem + "'");
                if (rs1.next()) {
                    cid = rs1.getString("courseid");
                }
                int id = Integer.parseInt(cid);
                rs = DBLoader.executeStatement("select * from notes where cid =" + id + " and sem ='" + sem + "' order by date desc");
                String tn = "";
                while (rs.next()) {
                    try {
                        String tid = rs.getString("tid");
                        String nid = rs.getString("noteid");
                        String title = rs.getString("title");
                        String date = rs.getString("date");
                        int ttid = Integer.parseInt(tid);
                        rs2 = DBLoader.executeStatement("select * from teachers where teacherid =" + ttid + "");
                        while (rs2.next()) {
                            tn = rs2.getString("tname");
                        }
                        data = data + ":#" + nid + ":#" + tn + ":#" + title + ":#" + date + "&";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", data);

        } else if (uri.contains("getcompletenotesdetails")) {
            String id = parms.getProperty("noteid");
            String tname = parms.getProperty("tname");
            String data = "";
            try {
                ResultSet rs;
                int nid = Integer.parseInt(id);
                rs = DBLoader.executeStatement("select * from notes where noteid =" + nid + "");
                while (rs.next()) {
                    try {
                        String title = rs.getString("title");
                        String detail = rs.getString("detail");
                        String file = rs.getString("file");
                        String date = rs.getString("date");
                        data = data + ":#" + title + ":#" + tname + ":#" + date + ":#" + detail + ":#" + file;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", data);

        } else if (uri.contains("StudentForgotPassword1")) {
            String sid = parms.getProperty("studentid");
            String ans = "";
            try {
                int id = Integer.parseInt(sid);
                ResultSet rs = DBLoader.executeStatement("select * from students where studentid=" + id + "");
                if (rs.next()) {
                    ans = "success";

                } else {
                    ans = "fail";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", ans);

        } else if (uri.contains("GenerateOTPStudent")) {
            String id = parms.getProperty("studentid");
            String numbers = "0123456789";
            String otp = "";
            String pn ="";
            ResultSet rs;
            try {
                Random r = new Random();
                int l = 4;
                int iid = Integer.parseInt(id);
                rs = DBLoader.executeStatement("select * from students where studentid ="+iid+"");
                if(rs.next()){
                    pn = rs.getString("sphoneno");
                }
                for (int i = 0; i < l; i++) {
                    otp = otp + numbers.charAt(r.nextInt(numbers.length()));
                }
                smssender.sendSMS(pn, "Your OTP is: "+otp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(otp);
            res = new Response(HTTP_OK, "text/plain", otp);
        
        }else if (uri.contains("GenerateOTPAdmin")) {
            String uid = parms.getProperty("username");
            String numbers = "0123456789";
            String otp = "";
            String pn ="";
            ResultSet rs;
            try {
                Random r = new Random();
                int l = 4;
                rs = DBLoader.executeStatement("select * from admin_login where username ='"+uid+"'");
                if(rs.next()){
                    pn = rs.getString("phone_no");
                }
                for (int i = 0; i < l; i++) {
                    otp = otp + numbers.charAt(r.nextInt(numbers.length()));
                }
                smssender.sendSMS(pn, "Your OTP is: "+otp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(otp);
            res = new Response(HTTP_OK, "text/plain", otp);
        
        }else if (uri.contains("GenerateOTPTeacher")) {
            String id = parms.getProperty("teacherid");
            String numbers = "0123456789";
            String otp = "";
            String pn ="";
            ResultSet rs;
            try {
                Random r = new Random();
                int l = 4;
                int iid = Integer.parseInt(id);
                rs = DBLoader.executeStatement("select * from teachers where teacherid ="+iid+"");
                if(rs.next()){
                    pn = rs.getString("tphoneno");
                }
                for (int i = 0; i < l; i++) {
                    otp = otp + numbers.charAt(r.nextInt(numbers.length()));
                }
                smssender.sendSMS(pn, "Your OTP is: "+otp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(otp);
            res = new Response(HTTP_OK, "text/plain", otp);
        
        } else if (uri.contains("StudentForgotPassword2")) {
            String sid = parms.getProperty("studentid");
            String npf = parms.getProperty("newpass");
            String ans = "";
            try {
                int id = Integer.parseInt(sid);
                ResultSet rs = DBLoader.executeStatement("select * from students where studentid=" + id + "");
                if (rs.next()) {
                    try {
                        String pass = rs.getString("spassword");
                        ans = "success";
                        rs.updateString("spassword", npf);
                        rs.updateRow();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", ans);

        }else if (uri.contains("teacherForgotPassword1")) {
            String tid = parms.getProperty("teacherid");
            String ans = "";
            try {
                int id = Integer.parseInt(tid);
                ResultSet rs = DBLoader.executeStatement("select * from teachers where teacherid=" + id + "");
                if (rs.next()) {
                    ans = "success";

                } else {
                    ans = "fail";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", ans);

        }else if (uri.contains("TeacherForgotPassword2")) {
            String tid = parms.getProperty("teacherid");
            String npf = parms.getProperty("newpass");
            String ans = "";
            try {
                int id = Integer.parseInt(tid);
                ResultSet rs = DBLoader.executeStatement("select * from teachers where teacherid=" + id + "");
                if (rs.next()) {
                    try {
                        String pass = rs.getString("tpassword");
                        ans = "success";
                        rs.updateString("tpassword", npf);
                        rs.updateRow();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", ans);

        }else if (uri.contains("AdminForgotPassword1")) {
            String un = parms.getProperty("useranme");
            String ans = "";
            try {
                ResultSet rs = DBLoader.executeStatement("select * from admin_login where username='" + un + "'");
                if (rs.next()) {
                    ans = "success";

                } else {
                    ans = "fail";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", ans);

        }else if (uri.contains("AdminForgotPassword2")) {
            String un = parms.getProperty("username");
            String npf = parms.getProperty("newpass");
            String ans = "";
            try {
                ResultSet rs = DBLoader.executeStatement("select * from admin_login where username='" + un + "'");
                if (rs.next()) {
                    try {
                        String pass = rs.getString("password");
                        ans = "success";
                        rs.updateString("password", npf);
                        rs.updateRow();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            res = new Response(HTTP_OK, "text/plain", ans);

        }
        return res;

    }
}
