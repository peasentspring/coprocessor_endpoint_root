    package com.test.coprocessor;  
      
    import java.io.IOException;  
    import java.util.Map;  
      
    import org.apache.hadoop.conf.Configuration;  
    import org.apache.hadoop.hbase.HBaseConfiguration;  
    import org.apache.hadoop.hbase.TableName;  
    import org.apache.hadoop.hbase.client.Connection;  
    import org.apache.hadoop.hbase.client.ConnectionFactory;  
    import org.apache.hadoop.hbase.client.HTable;  
    import org.apache.hadoop.hbase.client.coprocessor.Batch;  
    import org.apache.hadoop.hbase.ipc.BlockingRpcCallback;  
      
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
            conf.set("hbase.zookeeper.quorum",  
                    "datanode01.isesol.com,datanode02.isesol.com,datanode03.isesol.com,datanode04.isesol.com,cmserver.isesol.com");  
            conf.set("hbase.zookeeper.property.clientPort", "2181");  
            //   
            Connection conn = ConnectionFactory.createConnection(conf);  
            // 
            HTable table = (HTable) conn.getTable(TableName.valueOf("endpointTest"));  
            table.setOperationTimeout(10);  
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
                  
                count++;  
            }  
            //  
            System.out.println("count: " + count);  
            //  
            table.close();  
            conn.close();  
        }  
      
    }  
