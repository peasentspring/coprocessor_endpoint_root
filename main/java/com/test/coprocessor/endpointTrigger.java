    package com.test.coprocessor;  
      
    import java.io.BufferedReader;
    import java.io.FileReader;
    import java.io.IOException;  
    import java.util.ArrayList;  
    import java.util.List;  
      
    import org.apache.hadoop.conf.Configuration;  
    import org.apache.hadoop.hbase.Coprocessor;  
    import org.apache.hadoop.hbase.CoprocessorEnvironment;  
    import org.apache.hadoop.hbase.client.Durability;  
    import org.apache.hadoop.hbase.client.Get;  
    import org.apache.hadoop.hbase.client.HTable;  
    import org.apache.hadoop.hbase.client.Put;  
    import org.apache.hadoop.hbase.client.Scan;  
    import org.apache.hadoop.hbase.coprocessor.BaseRegionObserver;  
    import org.apache.hadoop.hbase.coprocessor.CoprocessorException;  
    import org.apache.hadoop.hbase.coprocessor.CoprocessorService;  
    import org.apache.hadoop.hbase.coprocessor.ObserverContext;  
    import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;  
    import org.apache.hadoop.hbase.protobuf.ResponseConverter;  
    import org.apache.hadoop.hbase.regionserver.InternalScanner;  
    import org.apache.hadoop.hbase.regionserver.wal.WALEdit;  
    import org.apache.hadoop.hbase.util.Bytes;  
    import org.apache.hadoop.hbase.Cell;    
    import org.apache.hadoop.hbase.CellUtil;    
    import com.google.protobuf.RpcCallback;  
    import com.google.protobuf.RpcController;  
    import com.google.protobuf.Service;  
    import com.test.coprocessor.Sum.SumRequest;  
    import com.test.coprocessor.Sum.SumResponse;  
    import com.test.coprocessor.Sum.SumService;  
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
   // import org.apache.commons.logging.Log;
   // import org.apache.commons.logging.LogFactory;

    import net.blackruffy.root.*;
    import static net.blackruffy.root.JRoot.*;
    import static net.blackruffy.root.Pointer.*;
      
    public class endpointTrigger extends SumService implements Coprocessor, CoprocessorService {  
      
        private static final Logger log = LoggerFactory.getLogger(endpointTrigger.class);
        private RegionCoprocessorEnvironment env;  
      
        @Override  
        public void getSum(RpcController controller, SumRequest request, RpcCallback<SumResponse> done) {  
            // TODO Auto-generated method stub  
      
            SumResponse response = null;  
            InternalScanner scanner = null;  
            try {  
//            	final TFile file = newTFile("/home/zyp/eventdb/zyp/test/testdata/run8200/09psip_stream001_run8200_file4.root", "READ");
//		final Pointer px = newInt(0);
//    		final TTree tree = TTree(file.get("tree"));
//    		long nev = tree.getEntries();
//		tree.setBranchAddress("x", px);
//
//		double sum = 0;
//		for( long ev=0; ev<nev; ev++ ) {
//		  tree.getEntry(ev);
//		  int x = px.getIntValue();
//		  sum += x;
//		}
//
//		tree.delete();
//		file.close();
                String FILENAME = "/home/spring/springz/test.txt";
		log.info("filename is test.txt");
		double sum = 0.0;
		try(BufferedReader br = new BufferedReader(new FileReader(FILENAME))){
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				log.info("sCurrentLine is " + sCurrentLine);
				sum += Double.parseDouble(sCurrentLine);
				log.info("sum is " + sum);
			}

		} catch (IOException e) {
			e.printStackTrace();
			log.info("exception " + e);
		}
                response = SumResponse.newBuilder().setSum(new Double(sum).longValue()).build();  
		log.info("response construct ok, sum is " + sum);
            } finally {  
                if (scanner != null) {  
                    try {  
                        scanner.close();  
                    } catch (IOException e) {  
                        e.printStackTrace();  
                    }  
                }  
            }  
      
            done.run(response);  
        }  
      
        public Service getService() {  
            // TODO Auto-generated method stub  
            return this;  
        }  
      
        public void start(CoprocessorEnvironment env) throws IOException {  
            // TODO Auto-generated method stub  
      
            if (env instanceof RegionCoprocessorEnvironment) {  
                this.env = (RegionCoprocessorEnvironment) env;  
            } else {  
                throw new CoprocessorException("can not start endpoint");  
            }  
      
        }  
      
        public void stop(CoprocessorEnvironment env) throws IOException {  
            // TODO Auto-generated method stub  
      
        }  
	
    }  
