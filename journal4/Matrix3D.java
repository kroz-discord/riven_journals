// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Matrix3D.java


class Matrix3D
{

    Matrix3D()
    {
        xx = 1.0F;
        yy = 1.0F;
        zz = 1.0F;
    }

    void scale(float f)
    {
        xx *= f;
        xy *= f;
        xz *= f;
        xo *= f;
        yx *= f;
        yy *= f;
        yz *= f;
        yo *= f;
        zx *= f;
        zy *= f;
        zz *= f;
        zo *= f;
    }

    void scale(float f, float f1, float f2)
    {
        xx *= f;
        xy *= f;
        xz *= f;
        xo *= f;
        yx *= f1;
        yy *= f1;
        yz *= f1;
        yo *= f1;
        zx *= f2;
        zy *= f2;
        zz *= f2;
        zo *= f2;
    }

    void translate(float f, float f1, float f2)
    {
        xo += f;
        yo += f1;
        zo += f2;
    }

    void yrot(double d)
    {
        d *= 0.017453292500000002D;
        double d1 = Math.cos(d);
        double d2 = Math.sin(d);
        float f = (float)((double)xx * d1 + (double)zx * d2);
        float f1 = (float)((double)xy * d1 + (double)zy * d2);
        float f2 = (float)((double)xz * d1 + (double)zz * d2);
        float f3 = (float)((double)xo * d1 + (double)zo * d2);
        float f4 = (float)((double)zx * d1 - (double)xx * d2);
        float f5 = (float)((double)zy * d1 - (double)xy * d2);
        float f6 = (float)((double)zz * d1 - (double)xz * d2);
        float f7 = (float)((double)zo * d1 - (double)xo * d2);
        xo = f3;
        xx = f;
        xy = f1;
        xz = f2;
        zo = f7;
        zx = f4;
        zy = f5;
        zz = f6;
    }

    void xrot(double d)
    {
        d *= 0.017453292500000002D;
        double d1 = Math.cos(d);
        double d2 = Math.sin(d);
        float f = (float)((double)yx * d1 + (double)zx * d2);
        float f1 = (float)((double)yy * d1 + (double)zy * d2);
        float f2 = (float)((double)yz * d1 + (double)zz * d2);
        float f3 = (float)((double)yo * d1 + (double)zo * d2);
        float f4 = (float)((double)zx * d1 - (double)yx * d2);
        float f5 = (float)((double)zy * d1 - (double)yy * d2);
        float f6 = (float)((double)zz * d1 - (double)yz * d2);
        float f7 = (float)((double)zo * d1 - (double)yo * d2);
        yo = f3;
        yx = f;
        yy = f1;
        yz = f2;
        zo = f7;
        zx = f4;
        zy = f5;
        zz = f6;
    }

    void zrot(double d)
    {
        d *= 0.017453292500000002D;
        double d1 = Math.cos(d);
        double d2 = Math.sin(d);
        float f = (float)((double)yx * d1 + (double)xx * d2);
        float f1 = (float)((double)yy * d1 + (double)xy * d2);
        float f2 = (float)((double)yz * d1 + (double)xz * d2);
        float f3 = (float)((double)yo * d1 + (double)xo * d2);
        float f4 = (float)((double)xx * d1 - (double)yx * d2);
        float f5 = (float)((double)xy * d1 - (double)yy * d2);
        float f6 = (float)((double)xz * d1 - (double)yz * d2);
        float f7 = (float)((double)xo * d1 - (double)yo * d2);
        yo = f3;
        yx = f;
        yy = f1;
        yz = f2;
        xo = f7;
        xx = f4;
        xy = f5;
        xz = f6;
    }

    void mult(Matrix3D matrix3d)
    {
        float f = xx * matrix3d.xx + yx * matrix3d.xy + zx * matrix3d.xz;
        float f1 = xy * matrix3d.xx + yy * matrix3d.xy + zy * matrix3d.xz;
        float f2 = xz * matrix3d.xx + yz * matrix3d.xy + zz * matrix3d.xz;
        float f3 = xo * matrix3d.xx + yo * matrix3d.xy + zo * matrix3d.xz + matrix3d.xo;
        float f4 = xx * matrix3d.yx + yx * matrix3d.yy + zx * matrix3d.yz;
        float f5 = xy * matrix3d.yx + yy * matrix3d.yy + zy * matrix3d.yz;
        float f6 = xz * matrix3d.yx + yz * matrix3d.yy + zz * matrix3d.yz;
        float f7 = xo * matrix3d.yx + yo * matrix3d.yy + zo * matrix3d.yz + matrix3d.yo;
        float f8 = xx * matrix3d.zx + yx * matrix3d.zy + zx * matrix3d.zz;
        float f9 = xy * matrix3d.zx + yy * matrix3d.zy + zy * matrix3d.zz;
        float f10 = xz * matrix3d.zx + yz * matrix3d.zy + zz * matrix3d.zz;
        float f11 = xo * matrix3d.zx + yo * matrix3d.zy + zo * matrix3d.zz + matrix3d.zo;
        xx = f;
        xy = f1;
        xz = f2;
        xo = f3;
        yx = f4;
        yy = f5;
        yz = f6;
        yo = f7;
        zx = f8;
        zy = f9;
        zz = f10;
        zo = f11;
    }

    void unit()
    {
        xo = 0.0F;
        xx = 1.0F;
        xy = 0.0F;
        xz = 0.0F;
        yo = 0.0F;
        yx = 0.0F;
        yy = 1.0F;
        yz = 0.0F;
        zo = 0.0F;
        zx = 0.0F;
        zy = 0.0F;
        zz = 1.0F;
    }

    void transform(float af[], float af1[], int i)
    {
        float f = xx;
        float f1 = xy;
        float f2 = xz;
        float f3 = xo;
        float f4 = yx;
        float f5 = yy;
        float f6 = yz;
        float f7 = yo;
        float f8 = zx;
        float f9 = zy;
        float f10 = zz;
        float f11 = zo;
        zmin = 1000000F;
        zmax = -zmin;
        for(int j = i * 3; (j -= 3) >= 0;)
        {
            float f13 = af[j];
            float f14 = af[j + 1];
            float f15 = af[j + 2];
            af1[j] = f13 * f + f14 * f1 + f15 * f2 + f3;
            af1[j + 1] = f13 * f4 + f14 * f5 + f15 * f6 + f7;
            float f12 = af1[j + 2] = f13 * f8 + f14 * f9 + f15 * f10 + f11;
            if(f12 < zmin)
                zmin = f12;
            if(f12 > zmax)
                zmax = f12;
        }

    }

    public float getZmax()
    {
        return zmax;
    }

    public float getZmin()
    {
        return zmin;
    }

    public String toString()
    {
        return "[" + xo + "," + xx + "," + xy + "," + xz + ";" + yo + "," + yx + "," + yy + "," + yz + ";" + zo + "," + zx + "," + zy + "," + zz + "]";
    }

    float xx;
    float xy;
    float xz;
    float xo;
    float yx;
    float yy;
    float yz;
    float yo;
    float zx;
    float zy;
    float zz;
    float zo;
    public float zmin;
    public float zmax;
    static final double pi = 3.1415926500000002D;
}
