// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Dot3DModel.java

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

class Dot3DModel
{

    Dot3DModel()
    {
        mat = new Matrix3D();
    }

    void SetDevAttributes(int i, int j, float f)
    {
        dev_xoffset = i;
        dev_yoffset = j;
        cam_focal_length = f;
    }

    Dot3DModel[] LinearMorph(Dot3DModel dot3dmodel, int i)
    {
        int k1 = 0;
        float f = 1E+08F;
        float af[] = vert;
        float af1[] = dot3dmodel.vert;
        float af2[] = tvert;
        for(int j = 0; j < nvert; j++)
        {
            int j1 = k1;
            float f7 = af[k1++];
            float f8 = af[k1++];
            float f9 = af[k1++];
            float f11 = f;
            int i1 = j;
            for(int l = j; l < nvert; l++)
            {
                float f1 = af1[j1++];
                float f3 = af1[j1++];
                float f5 = af1[j1++];
                float f12 = f1 - f7;
                float f14 = f3 - f8;
                float f16 = f5 - f9;
                float f10 = (float)Math.sqrt(f12 * f12 + f14 * f14 + f16 * f16);
                if(f10 < f11)
                {
                    f11 = f10;
                    i1 = l;
                }
            }

            j1 = i1 * 3;
            float f2 = af1[j1++];
            float f4 = af1[j1++];
            float f6 = af1[j1++];
            float f13 = f2 - f7;
            float f15 = f4 - f8;
            float f17 = f6 - f9;
            k1 -= 3;
            float f18 = i + 1;
            af2[k1++] = f13 / f18;
            af2[k1++] = f15 / f18;
            af2[k1++] = f17 / f18;
            k1 -= 3;
            j1 -= 3;
            af1[j1++] = af1[k1++];
            af1[j1++] = af1[k1++];
            af1[j1++] = af1[k1++];
            k1 -= 3;
            af1[k1++] = f2;
            af1[k1++] = f4;
            af1[k1++] = f6;
        }

        float af3[] = af;
        Dot3DModel adot3dmodel[] = new Dot3DModel[i];
        for(int k = 0; k < i; k++)
        {
            adot3dmodel[k].SetDevAttributes(dev_xoffset, dev_yoffset, cam_focal_length);
            float af4[] = adot3dmodel[k].vert = new float[3 * nvert];
            adot3dmodel[k].tvert = new float[3 * nvert];
            adot3dmodel[k].nvert = nvert;
            nvert *= 3;
            for(int l1 = 0; l1 < nvert; l1++)
                af4[l1] = af3[l1] + af2[l1];

            af3 = af4;
        }

        return adot3dmodel;
    }

    void genRandomSphere(int i, float f, float f1)
    {
        Random random = new Random();
        nvert = i * 8;
        if(vert == null)
            vert = new float[3 * nvert];
        if(tvert == null)
            tvert = new float[3 * nvert];
        float f2 = f / 2.0F;
        vert[0] = -f2;
        vert[1] = -f2;
        vert[2] = -f2;
        vert[3] = f2;
        vert[4] = -f2;
        vert[5] = -f2;
        vert[6] = f2;
        vert[7] = f2;
        vert[8] = -f2;
        vert[9] = -f2;
        vert[10] = f2;
        vert[11] = -f2;
        vert[12] = -f2;
        vert[13] = -f2;
        vert[14] = f2;
        vert[15] = f2;
        vert[16] = -f2;
        vert[17] = f2;
        vert[18] = f2;
        vert[19] = f2;
        vert[20] = f2;
        vert[21] = -f2;
        vert[22] = f2;
        vert[23] = f2;
        i--;
        int k = 24;
        for(int i1 = 0; i1 < i; i1++)
        {
            mat.unit();
            float f3 = 1.0F - random.nextFloat() * f1;
            mat.scale(f3);
            float f4 = random.nextFloat() * 360F;
            float f5 = random.nextFloat() * 360F;
            float f6 = random.nextFloat() * 360F;
            mat.xrot(f4);
            mat.yrot(f5);
            mat.zrot(f6);
            mat.transform(vert, tvert, 8);
            int l = 0;
            for(int j = 0; j < 8; j++)
            {
                vert[k++] = tvert[l++];
                vert[k++] = tvert[l++];
                vert[k++] = tvert[l++];
            }

        }

    }

    void InitGrid(int i, int j, float f, float f1)
    {
        nvert = i * j;
        if(vert == null)
            vert = new float[3 * nvert];
        if(tvert == null)
            tvert = new float[3 * nvert];
        float f2;
        float f4 = f2 = (float)(-(((double)(float)i / 2D) * (double)f));
        float f3 = (float)(-(((double)(float)j / 2D) * (double)f1));
        int l = 0;
        while(j-- > 0) 
        {
            for(int k = 0; k < i; k++)
            {
                vert[l++] = f2;
                vert[l++] = f3;
                vert[l++] = 0.0F;
                f2 += f;
            }

            f2 = f4;
            f3 += f1;
        }
    }

    private void hf(int i, int j, int k)
    {
        if(k == 1)
        {
            float f2 = htFd[j][i];
            float f4 = htFd[j][i + 1];
            float f6 = htFd[j + 1][i];
            float f8 = htFd[j + 1][i + 1];
            float f = (f2 + f4 + f6 + f8) / 4F;
            htFd[j][i] = (f2 + f) / 2.0F;
            htFd[j][i + 1] = (f4 + f) / 2.0F;
            htFd[j + 1][i] = (f6 + f) / 2.0F;
            htFd[j + 1][i + 1] = (f8 + f) / 2.0F;
            return;
        } else
        {
            int l = --k;
            float f3 = htFd[j][i];
            float f5 = htFd[j][i + k];
            float f7 = htFd[j + k][i];
            float f9 = htFd[j + k][i + k];
            float f1 = (f3 + f5 + f7 + f9) / 4F;
            k = l >> 1;
            k--;
            htFd[j + k][i + k] = (float)((double)f1 + (((double)randFloat.nextFloat() - 0.5D) / (double)subScale) * (double)initRange * 3D) / 2.0F;
            htFd[j][i + k] = (float)((double)((f3 + f5) / 2.0F) + (((double)randFloat.nextFloat() - 0.5D) / (double)subScale) * (double)initRange) / 2.0F;
            htFd[j + l][i + k] = (float)((double)((f7 + f9) / 2.0F) + (((double)randFloat.nextFloat() - 0.5D) / (double)subScale) * (double)initRange) / 2.0F;
            htFd[j + k][i + l] = (float)((double)((f5 + f9) / 2.0F) + (((double)randFloat.nextFloat() - 0.5D) / (double)subScale) * (double)initRange) / 2.0F;
            htFd[j + k][i] = (float)((double)((f3 + f7) / 2.0F) + (((double)randFloat.nextFloat() - 0.5D) / (double)subScale) * (double)initRange) / 2.0F;
            k++;
            subScale <<= 1;
            hf(i, j, k);
            hf(i + k, j, k);
            hf(i, j + k, k);
            hf(i + k, j + k, k);
            return;
        }
    }

    void blurhf(int i, int j)
    {
        if(bhf == null)
            bhf = new float[j][j];
        while(i-- > 0) 
        {
            for(int k1 = 1; k1 < j - 1; k1++)
            {
                for(int k = 1; k < j - 1; k++)
                    bhf[k1][k] = (float)((double)(htFd[k1 - 1][k - 1] + htFd[k1 - 1][k] + htFd[k1 - 1][k + 1] + htFd[k1][k - 1] + htFd[k1][k] + htFd[k1][k + 1] + htFd[k1 + 1][k - 1] + htFd[k1 + 1][k] + htFd[k1 + 1][k + 1]) / 9D);

            }

            int l1 = 0;
            for(int l = 1; l < j - 1; l++)
                bhf[l1][l] = (htFd[l1][l - 1] + htFd[l1][l] + htFd[l1][l + 1] + htFd[l1 + 1][l - 1] + htFd[l1 + 1][l] + htFd[l1 + 1][l + 1]) / 6F;

            l1 = j - 1;
            for(int i1 = 1; i1 < j - 1; i1++)
                bhf[l1][i1] = (htFd[l1 - 1][i1 - 1] + htFd[l1 - 1][i1] + htFd[l1 - 1][i1 + 1] + htFd[l1][i1 - 1] + htFd[l1][i1] + htFd[l1][i1 + 1]) / 6F;

            int j1 = 0;
            for(int i2 = 1; i2 < j - 1; i2++)
                bhf[i2][j1] = (htFd[i2 - 1][j1] + htFd[i2 - 1][j1 + 1] + htFd[i2][j1] + htFd[i2][j1 + 1] + htFd[i2 + 1][j1] + htFd[i2 + 1][j1 + 1]) / 6F;

            j1 = j - 1;
            for(int j2 = 1; j2 < j - 1; j2++)
                bhf[j2][j1] = (htFd[j2 - 1][j1 - 1] + htFd[j2 - 1][j1] + htFd[j2][j1 - 1] + htFd[j2][j1] + htFd[j2 + 1][j1 - 1] + htFd[j2 + 1][j1]) / 6F;

            bhf[0][0] = (htFd[0][0] + htFd[0][1] + htFd[1][0] + htFd[1][1]) / 4F;
            bhf[0][j - 1] = (htFd[0][j - 1] + htFd[0][j - 2] + htFd[1][j - 1] + htFd[1][j - 2]) / 4F;
            bhf[j - 1][0] = (htFd[j - 1][0] + htFd[j - 2][0] + htFd[j - 1][1] + htFd[j - 2][1]) / 4F;
            bhf[j - 1][j - 1] = (htFd[j - 1][j - 1] + htFd[j - 2][j - 1] + htFd[j - 1][j - 2] + htFd[j - 2][j - 2]) / 4F;
            float af[][] = bhf;
            bhf = htFd;
            htFd = af;
        }
    }

    void InithtFd(int i, float f)
    {
        if(htFd == null)
            htFd = new float[i][i];
        if(randFloat == null)
            randFloat = new Random();
        initRange = (float)i * f * 4F;
        htFd[0][0] = randFloat.nextFloat() * initRange - initRange / 2.0F;
        htFd[0][i - 1] = randFloat.nextFloat() * initRange - initRange / 2.0F;
        htFd[i - 1][0] = randFloat.nextFloat() * initRange - initRange / 2.0F;
        htFd[i - 1][i - 1] = randFloat.nextFloat() * initRange - initRange / 2.0F;
        subScale = 2;
        hf(0, 0, i);
        blurhf(3, i);
        InitGrid(i, i, f, f);
        int j = 2;
        for(int k = 0; k < i; k++)
        {
            for(int l = 0; l < i; l++)
            {
                vert[j] = htFd[k][l];
                j += 3;
            }

        }

    }

    void Transform()
    {
        if(mat != null && vert != null)
            mat.transform(vert, tvert, nvert);
    }

    void TransformStore()
    {
        if(mat != null && vert != null)
            mat.transform(vert, tvert, nvert);
        float af[] = vert;
        vert = tvert;
        tvert = af;
    }

    void Draw(Graphics g)
    {
        int k = 0;
        float f4 = cam_focal_length;
        float f5 = -f4;
        float f6 = mat.getZmin();
        if(f6 < f5)
            f6 = f5;
        float f7 = mat.getZmax();
        float f8 = f7 - f6;
        for(int j = 0; j < nvert; j++)
        {
            float f = tvert[k++];
            float f1 = tvert[k++];
            float f2 = tvert[k++];
            if(f2 > f5)
            {
                float f3 = f2 + f4;
                int l = (int)((cam_focal_length * f) / f3) + dev_xoffset;
                int i1 = (int)((cam_focal_length * f1) / f3) + dev_xoffset;
                int i = 255 - (int)((255F * (f2 - f6)) / f8);
                if(i > 255 || i < 0)
                    i = 255;
                g.setColor(new Color(i, i, i));
                i >>= 5;
                int j1 = (i += 2) >> 1;
                g.fillRect(l - j1, i1 - j1, i, i);
            }
        }

    }

    public String toString()
    {
        int l = 0;
        StringBuffer stringbuffer = new StringBuffer(nvert * 20 * 2);
        int k = 0;
        stringbuffer.append("vert[] = \n");
        for(int i = 0; i < nvert; i++)
        {
            float f = vert[k++];
            float f2 = vert[k++];
            float f4 = vert[k++];
            stringbuffer.append("[" + f + "," + f2 + "," + f4 + "] ");
            if(stringbuffer.length() - l > 80)
            {
                stringbuffer.append("\n");
                l = stringbuffer.length();
            }
        }

        stringbuffer.append("\n");
        k = 0;
        stringbuffer.append("tvert[] = \n");
        for(int j = 0; j < nvert; j++)
        {
            float f1 = tvert[k++];
            float f3 = tvert[k++];
            float f5 = tvert[k++];
            stringbuffer.append("[" + f1 + "," + f3 + "," + f5 + "] ");
            if(stringbuffer.length() - l > 70)
            {
                stringbuffer.append("\n");
                l = stringbuffer.length();
            }
        }

        stringbuffer.append("\n");
        return stringbuffer.toString();
    }

    Matrix3D mat;
    float vert[];
    float tvert[];
    int nvert;
    int dev_xoffset;
    int dev_yoffset;
    float cam_focal_length;
    private float htFd[][];
    private float bhf[][];
    private Random randFloat;
    private float initRange;
    private int subScale;
}
