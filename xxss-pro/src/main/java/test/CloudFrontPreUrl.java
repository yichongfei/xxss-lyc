package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

import com.amazonaws.Protocol;
import com.amazonaws.services.cloudfront.AmazonCloudFrontAsync;
import com.amazonaws.services.cloudfront.AmazonCloudFrontAsyncClient;
import com.amazonaws.services.cloudfront.AmazonCloudFrontAsyncClientBuilder;
import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.cloudfront.model.CloudFrontOriginAccessIdentity;
import com.amazonaws.services.cloudfront.util.SignerUtils;
import com.amazonaws.services.s3.internal.ServiceUtils;
import com.amazonaws.util.StringUtils;

public class CloudFrontPreUrl {
	public static void main(String[] args) throws Exception, IOException {
		

		String distributionDomain = "dhfoia6gap064.cloudfront.net";
		String privateKeyFilePath = "C:\\Users\\Administrator\\Desktop\\aws登陆\\pk-APKAI37IRC7HLOI6ALDQ.pem";
		String s3ObjectKey = "Nicole Aniston/2018-08-10-02/形形色色专属VIP 视频 -- 170316_1528_480P_600K_109772792d35/mp4/170316_1528_480P_600K_109772792.mp4";
		String policyResourcePath = "http://" + distributionDomain + "/" + s3ObjectKey;
		
		String signedURLWithCannedPolicy = CloudFrontUrlSigner.getSignedURLWithCannedPolicy(SignerUtils.Protocol.http, distributionDomain, new File(privateKeyFilePath), s3ObjectKey, "APKAI37IRC7HLOI6ALDQ", new Date(System.currentTimeMillis()+60000));
		System.out.println(signedURLWithCannedPolicy);
	}
}
