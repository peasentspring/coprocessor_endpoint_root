    package com.test.coprocessor;  
      
    import java.io.IOException;  
    import java.util.Map;  
      
    import org.apache.hadoop.conf.Configuration;  
    import org.apache.hadoop.hbase.HBaseConfiguration;  
    import org.apache.hadoop.hbase.TableName;  
    import org.apache.hadoop.hbase.Coprocessor;
    import org.apache.hadoop.hbase.HTableDescriptor;
    import org.apache.hadoop.hbase.client.Connection;  
    import org.apache.hadoop.hbase.client.ConnectionFactory;  
    import org.apache.hadoop.hbase.client.HTable;  
    import org.apache.hadoop.hbase.client.Admin;  
    import org.apache.hadoop.hbase.client.coprocessor.Batch;  
    import org.apache.hadoop.hbase.ipc.BlockingRpcCallback;  
    import org.apache.hadoop.fs.Path;
      
    import com.google.protobuf.ServiceException;  
    import com.test.coprocessor.Sum.SumRequest;  
    import com.test.coprocessor.Sum.SumResponse;  
    import com.test.coprocessor.Sum.SumService;;  
      
      
  
    public class Sumclient {  
      
        public static void main(String[] args) throws ServiceException, Throwable {  
            long sum = 0L;  
            int count = 0;  
            //   
            Configuration conf = HBaseConfiguration.create();  
	    conf.set("hbase.zookeeper.quorum", "127.0.0.1");
	    conf.set("hbase.zookeeper.property.clientPort", "2185");
	    conf.set("hbase.master", "192.168.0.178:61000");
            //conf.set("hbase.zookeeper.quorum",  
            //        "datanode01.isesol.com,datanode02.isesol.com,datanode03.isesol.com,datanode04.isesol.com,cmserver.isesol.com");  
            //conf.set("hbase.zookeeper.property.clientPort", "2185");  
            //   
            Connection conn = ConnectionFactory.createConnection(conf);  
	    String path = "hdfs://192.168.0.178:9000/springz/test-1.0-SNAPSHOT-jar-with-dependencies.jar";
            // 
	    String tableName = "endpointTest";
            HTable table = (HTable) conn.getTable(TableName.valueOf(tableName));  
	    System.out.println("table is: "+table);
            //table.setOperationTimeout(10);  
	    Admin admin = conn.getAdmin();
	    System.out.println("admin is: "+ admin);
	    admin.disableTable(TableName.valueOf(tableName));
	    System.out.println("disable table");
	    HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
//	    hTableDescriptor.setValue("COPROCESSOR$1", path + "|"
//				+ endpointTriger.class + "|"
//				+ Coprocessor.PRIORITY_USER);
	    if (!hTableDescriptor.hasCoprocessor(endpointTrigger.class.getCanonicalName())){
	    hTableDescriptor.addCoprocessor(endpointTrigger.class.getCanonicalName(), new Path(path), Coprocessor.PRIORITY_USER, null);
	    System.out.println("add coprocessor");
	    admin.modifyTable(TableName.valueOf(tableName), hTableDescriptor);
	    admin.enableTable(TableName.valueOf(tableName));
	    System.out.println("enable table");
	    }
            // 
            final SumRequest request = SumRequest.newBuilder().setFamily("data").setColumn("c1").build();  
              
              
            System.out.println("start to invoke result");  
              
            //  
            Map<byte[], Long> result = table.coprocessorService(Sum.SumService.class, null, null,   
                    new Batch.Call<Sum.SumService, Long>() {  
      
                        public Long call(SumService service) throws IOException {  
                            BlockingRpcCallback<SumResponse> rpcCallback = new BlockingRpcCallback<SumResponse>();  
                            service.getSum(null, request, rpcCallback);  
                            SumResponse response = (SumResponse) rpcCallback.get();  
                            return response.hasSum() ? response.getSum() : 0L;  
                        }  
            });  
              
            System.out.println("satrt to count the value");  
            //
            for (Long v : result.values()) {  
                sum += v;     
                count++;  
            }  
            //  
            System.out.println("count: " + count + "  sum: " + sum);  
            //  
	    hTableDescriptor.removeCoprocessor(endpointTrigger.class.getCanonicalName());
            table.close();  
            conn.close();  
        }  
      
    }  
